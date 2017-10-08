package com.usu.tinyservice.annotations;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;

import com.usu.tinyservice.network.NetUtils;


/**
 * class to create server and client objects.  
 * 
 * @author minhld
 *
 */
public class MobileServiceCreator {
	static final String REP_STRING = "!@#$%^";
	static String classInstance;
	
	public static void generateServer(ProcessingEnvironment env, TypeElement type) {
		// get service attributes
		MobileService classService = type.getAnnotation(MobileService.class);
		// CommModel commModel = classService.commModel();
		TransmitType transType = classService.transmitType();
		
		String fullClassName = type.getQualifiedName().toString();
		int lastDotIndex = fullClassName.lastIndexOf('.');
		String packageName = fullClassName.substring(0, lastDotIndex);
		String className = fullClassName.substring(lastDotIndex + 1);
		
		String serverClassName = className + "Server";
		
		// get list of inner methods
		List<? extends Element> methods = type.getEnclosedElements();
		
		try {
			JavaFileObject builderFile = env.getFiler().createSourceFile(serverClassName);
			PrintWriter writer = new PrintWriter(builderFile.openWriter());

			// print package and default imports
			writer.println("package " + packageName + ";");
			writer.println();
			writer.println("import com.usu.tinyservice.messages.RequestMessage;");
			writer.println("import com.usu.tinyservice.messages.ResponseMessage;");
			writer.println("import com.usu.tinyservice.network.JSONHelper;");
			writer.println("import com.usu.tinyservice.network.NetUtils;");
			writer.println("import com.usu.tinyservice.network.Responder;");
			writer.println();
			
			// declare class prototype 
			writer.println("public class " + serverClassName + " {");
			
			// declare the original service & network class 
			classInstance = className.toLowerCase();
			writer.println("  " + className + " " + classInstance + ";"); 
			writer.println("  ResponderX resp;");
			writer.println();
			
			// define the server constructor
			writer.println("  public " + serverClassName + "() {");
			writer.println("    " + classInstance + " = new " + className + "();");
			writer.println("    resp = new ResponderX();");
			writer.println("    resp.start();");
			writer.println("  }");
			writer.println();
			
			// define the extended Responder
			writer.println("  class ResponderX extends Responder {");
			writer.println("    @Override");
			writer.println("    public void respond(byte[] req) {");
			
			if (transType == TransmitType.JSON) {
				// if JSON is needed
				printJSONResponder(writer, type, methods);
			} else if (transType == TransmitType.Binary) {
				// ... Binary ...
				printBinaryResponder(writer, type, methods);
			}
			
			// the last part
			writer.println("    }");
			writer.println("  }");
			writer.println("}");
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * print out the Binary part in the Responder class
	 * 
	 * @param writer
	 * @param e
	 * @param methods
	 */
	private static void printBinaryResponder(PrintWriter writer, Element e, List<? extends Element> methods) {
		// define all the function wrapper
		for (int i = 0; i < methods.size(); i++) {
			printSyncServerHandler(writer, methods.get(i));
		}
	}

	/**
	 * 
	 * @param writer
	 * @param e
	 */
	private static void printSyncServerHandler(PrintWriter writer, Element e) {
		if (e.getAnnotation(ServiceMethod.class) != null && e instanceof ExecutableElement) {
			
		}
	}
	
	/**
	 * print out the JSON part in the Responder class
	 * 
	 * @param writer
	 * @param e
	 * @param methods
	 */
	private static void printJSONResponder(PrintWriter writer, Element e, List<? extends Element> methods) {
		writer.println("      String reqJSON = new String(req);");
		writer.println("      RequestMessage reqMsg = JSONHelper.getRequest(reqJSON);");
		writer.println();
		
		// define the switch - where all the functions are iterated here
		writer.println("      switch (reqMsg.functionName) {");

		// define all the function wrapper
		for (int i = 0; i < methods.size(); i++) {
			printJSONAsyncServerHandler(writer, methods.get(i));
		}
		
		// close the part
		writer.println("      }");
	}
	
	/**
	 * print out one function block in JSON support 
	 * 
	 * @param writer
	 * @param e
	 */
	private static void printJSONAsyncServerHandler(PrintWriter writer, Element e) {
		ServiceMethod sm = e.getAnnotation(ServiceMethod.class);
		
		// only accept functions having annotation, function and sync_mode is ASYNC
		if (e.getAnnotation(ServiceMethod.class) != null && e instanceof ExecutableElement && sm.syncMode() == SyncMode.Async) {
			String funcPrepare = printFuncCall(e);
			
			String jSONConvert = "        // convert to JSON\n" +
								 "        String respJSON = JSONHelper.createResponse(respMsg);\n" + 
								 "        send(respJSON);\n";
			funcPrepare = funcPrepare.replace(REP_STRING, jSONConvert);
			writer.println(funcPrepare);
		}
	}
	
	private static String printFuncCall(Element e) {
		ExecutableElement ee = (ExecutableElement) e;
		String funcName = ee.getSimpleName().toString();
		String funcCall = "";
		
		funcCall += "      case \"" + funcName + "\": {\n";
		
		List<? extends VariableElement> ves = ee.getParameters();
		for (int i = 0; i < ves.size(); i++) {
			funcCall += printInputParam(ves.get(i)) + "\n";
		}
		
		String retType = getType(ee.getReturnType().toString());
		
		funcCall += "        // start calling function \"" + funcName + "\"\n";
		funcCall += "        " + ee.getReturnType().toString() + " rets = " + classInstance + "." + funcName + "(";
		for (int i = 0; i < ves.size(); i++) {
			funcCall += ves.get(i).getSimpleName() + (i < ves.size() - 1 ? ", " : "");
		}
		funcCall += ");\n";
		funcCall += "        String retType = \"" + retType.replace("[]", "") + "\";\n";
		funcCall += "        String[] retValues = NetUtils.getStringArray(rets);\n";
		funcCall += "        ResponseMessage respMsg = new ResponseMessage(reqMsg.messageId, reqMsg.functionName, retType, retValues);\n\n"; 
				
		funcCall += REP_STRING;
		funcCall += "        break;\n" +
					"      }\n";
		return funcCall;
	}
	
	/**
	 * help converting parameter into the general format 
	 * 
	 * @param e
	 * @return
	 */
	private static String printInputParam(VariableElement e) {
		String inParamsStr = "";
		
		// define variable name
		String vName = e.getSimpleName().toString();
		String vNames = vName + "s";
		
		// define variable types
		String vFullType = e.asType().toString();
		String vType = getType(vFullType).replace("[]", "");
		
		// assign value from a parameter to an array
		inParamsStr += "        // for variable " + "\"" + vName + "\"\n";
		inParamsStr += "        " + vType + "[] " + vNames + " = new " + vType + "[reqMsg.inParams[0].values.length];\n";
		inParamsStr += "        for (int i = 0; i < reqMsg.inParams[0].values.length; i++) {\n";
		inParamsStr += "          " + vNames + "[i] = " + convertType(vType, "reqMsg.inParams[0].values[i]") + ";\n";  
		inParamsStr += "        }\n";

		// assign values from the array to the original parameter 
		if (vFullType.contains("[]")) {
			// variable is an array
			inParamsStr += "        " + vType + "[] " + vName + " = " + vNames + ";\n";	
		} else {
			// variable is a single value
			inParamsStr += "        " + vType + " " + vName + " = " + vNames + "[0];\n";
		}
		return inParamsStr;
	}
	
	
	/**
	 * 
	 * @param env
	 * @param type
	 */
	public static void generateClient(ProcessingEnvironment env, TypeElement type) {
		
	}

	/**
	 * convert from primitive type like { float, boolean } to 
	 * Object type like { Float, Boolean }
	 * 
	 * @param type
	 * @param valStr
	 * 
	 * @return
	 */
	private static String convertType(String type, String valStr) {
		switch (type) {
			case "byte": {
				return "Byte.parseByte(" + valStr + ")";
			}
			case "char": {
				return valStr + ".charAt[0]";
			}
			case "short": {
				return "Short.parseShort(" + valStr + ")";
			}
			case "int": {
				return "Integer.parseInt(" + valStr + ")";
			}
			case "long": {
				return "Long.parseLong(" + valStr + ")";
			}
			case "float": {
				return "Float.parseFloat(" + valStr + ")";
			}
			case "double": {
				return "Double.parseDouble(" + valStr + ")";
			}
			case "String": {
				return valStr;
			}
			case "boolean": {
				return "Boolean.parseBoolean(" + valStr + ")";
			}
		}
		return "";
	}
	
	/**
	 * remove the package name from the full-name type 
	 * 
	 * @param fullType
	 * @return
	 */
	private static String getType(String fullType) {
		int lastDot = fullType.lastIndexOf('.');
		return fullType.substring(lastDot + 1);
	}
}
