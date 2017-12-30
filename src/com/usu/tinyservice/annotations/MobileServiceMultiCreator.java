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


/**
 * class to create server and client objects.  
 * 
 * @author minhld
 *
 */
public class MobileServiceMultiCreator {
	static final String REP_STRING = "!@#$%^";
	static String classInstance;
	
	/**
	 * generator of a server 
	 * 
	 * @param env
	 * @param type
	 */
	public static void generateWorker(ProcessingEnvironment env, TypeElement type) {
		// get service attributes
		// MobileService classService = type.getAnnotation(MobileService.class);
		// CommModel commModel = classService.commModel();
		// TransmitType transType = classService.transmitType();
		
		String fullClassName = type.getQualifiedName().toString();
		int lastDotIndex = fullClassName.lastIndexOf('.');
		String packageName = fullClassName.substring(0, lastDotIndex);
		String className = fullClassName.substring(lastDotIndex + 1);
		
		String serverClassName = className + "Worker";
		
		// get list of inner methods
		List<? extends Element> methods = type.getEnclosedElements();
		
		try {
			JavaFileObject builderFile = env.getFiler().createSourceFile(serverClassName);
			PrintWriter writer = new PrintWriter(builderFile.openWriter());

			// print package and default imports
			writer.println("package " + packageName + ";");
			writer.println();
			writer.println("import com.usu.tinyservice.messages.binary.RequestMessage;");
			writer.println("import com.usu.tinyservice.messages.binary.ResponseMessage;");
			writer.println("import com.usu.tinyservice.network.NetUtils;");
			writer.println("import com.usu.tinyservice.network.Worker;");
			writer.println();
			
			// declare class prototype 
			writer.println("public class " + serverClassName + " {");
			
			// declare the original service & network class 
			classInstance = className.toLowerCase();
			writer.println("  " + className + " " + classInstance + ";"); 
			writer.println();
			
			// define the server constructor
			writer.println("  public " + serverClassName + "(String brokerIp) {");
			writer.println("    " + classInstance + " = new " + className + "();");
			writer.println("    new WorkerX(brokerIp);");
			writer.println("  }");
			writer.println();
			
			// define the extended Responder
			writer.println("  class WorkerX extends Worker {");
			writer.println("    @Override");
			writer.println("    public byte[] resolveRequest(byte[] packageBytes) {");
			
			// String serverResponder = printAsyncServerResponder(transType, type, methods);
			String serverResponder = printAsyncServerResponder(type, methods);
			writer.println(serverResponder);
			writer.println("    }\n");
			
			writer.println("    @Override");
			writer.println("    public byte[] info() {");
			String infoFunc = printInfoFunc(methods);
			writer.println(infoFunc);
			
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
	 * print out the JSON part in the Responder class
	 * 
	 * @param e
	 * @param methods
	 */
	// private static String printAsyncServerResponder(TransmitType transType, Element e, List<? extends Element> methods) {
	private static String printAsyncServerResponder(Element e, List<? extends Element> methods) {
		String serverResponder = "";
		String reqConvert = "";
		reqConvert = "      byte[] respBytes = null;\n\n" +
					 "      // get request message\n" + 
				 	 "      RequestMessage reqMsg = (RequestMessage) NetUtils.deserialize(req);\n\n";
		
		// define the switch - where all the functions are iterated here
		serverResponder += reqConvert + 
						 "      switch (reqMsg.functionName) {\n";

		// define all the function wrapper
		for (int i = 0; i < methods.size(); i++) {
			serverResponder += printAsyncFunctionHandler(methods.get(i));
		}
		
		// close the part
		serverResponder += "      }\n\n" + 
						   "      return respBytes;\n";
		return serverResponder;
	}
	
	/**
	 * print out one function block in JSON support 
	 * 
	 * @param e
	 */
	// private static String printAsyncFunctionHandler(TransmitType transType, Element e) {
	private static String printAsyncFunctionHandler(Element e) {		
		ServiceMethod sm = e.getAnnotation(ServiceMethod.class);
		
		// only accept functions having annotation, function and sync_mode is ASYNC
		// if (e.getAnnotation(ServiceMethod.class) != null && e instanceof ExecutableElement && sm.syncMode() == SyncMode.Async) {
		if (sm != null && e instanceof ExecutableElement) {
			String funcPrepare = printFuncCall(e);
			
			String respConvert = "        // convert to binary array\n" +
						  	  	 "        byte[] respBytes = NetUtils.serialize(respMsg);\n";
			
			funcPrepare = funcPrepare.replace(REP_STRING, respConvert);
			return funcPrepare;
		}
		return "";
	}
	
	/**
	 * print the function call part of the Responder
	 * 
	 * @param e
	 * @return
	 */
	private static String printFuncCall(Element e) {
		ExecutableElement ee = (ExecutableElement) e;
		String funcName = ee.getSimpleName().toString();
		String funcCall = "";
		
		funcCall += "      case \"" + funcName + "\": {\n";
		
		List<? extends VariableElement> ves = ee.getParameters();
		for (int i = 0; i < ves.size(); i++) {
			funcCall += printInputParam(ves.get(i), i) + "\n";
		}
		
		// String retType = getOnlyName(ee.getReturnType().toString());
		String retType = ee.getReturnType().toString(); 
		
		funcCall += "        // start calling function \"" + funcName + "\"\n";
		funcCall += "        " + ee.getReturnType().toString() + " rets = " + classInstance + "." + funcName + "(";
		for (int i = 0; i < ves.size(); i++) {
			funcCall += ves.get(i).getSimpleName() + (i < ves.size() - 1 ? ", " : "");
		}
		funcCall += ");\n";
		// funcCall += "        String retType = \"" + retType.replace("[]", "") + "\";\n";
		funcCall += "        String retType = \"" + retType + "\";\n";
		// funcCall += "        String[] retValues = NetUtils.getStringArray(rets);\n";
		funcCall += "        ResponseMessage respMsg = new ResponseMessage(reqMsg.messageId, reqMsg.functionName, retType, rets);\n\n"; 
				
		funcCall += REP_STRING;
		funcCall += "        break;\n" +
					"      }\n";
		return funcCall;
	}
	
	/**
	 * help converting parameter into the general format 
	 * 
	 * @param e
	 * @param idx
	 * 
	 * @return
	 */
	private static String printInputParam(VariableElement e, int idx) {
		String inParamsStr = "";
		
		// define variable name
		String vName = e.getSimpleName().toString();
		String vNames = vName + "s";
		
		// define variable types
		String vFullType = e.asType().toString();
		// String vType = getOnlyName(vFullType).replace("[]", "");
		String vType = vFullType.replace("[]", "");
		
		// assign value from a parameter to an array
		inParamsStr += "        // for variable " + "\"" + vName + "\"\n";
		inParamsStr += "        " + vType + "[] " + vNames + " = new " + vType + "[reqMsg.inParams[" + idx + "].values.length];\n";
		inParamsStr += "        for (int i = 0; i < reqMsg.inParams[" + idx + "].values.length; i++) {\n";
		inParamsStr += "          " + vNames + "[i] = " + convertType(vType, "reqMsg.inParams[" + idx + "].values[i]") + ";\n";  
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
	
	
	private static String printInfoFunc(List<? extends Element> methods) {
		String infoStr = "      String json =\n" +
						 "        \"{\" +\n" +
						 "          \"\\\"code\\\" : \\\"REGISTER\\\",\" +\n" +
						 "          \"\\\"id\\\" : \\\"\" + worker.workerId + \"\\\",\" +\n" +
						 "          \"\\\"functions\\\" : [\" +\n";
		
		// define all the function wrapper
		String funcDef = "";
		ExecutableElement m;
		List<? extends VariableElement> ves;
		for (int i = 0; i < methods.size(); i++) {
			if (methods.get(i) instanceof ExecutableElement) {
				m = (ExecutableElement) methods.get(i);
				funcDef = 
						"            \"{\" +" +
						"              \"\\\"functionName\\\" : \\\"" + m.getSimpleName().toString() + "\\\",\" +";
								
				ves = m.getParameters();
						
				funcDef =	
						"              \"\\\"inParams\\\" : [\\\"java.lang.String\\\"],\" +" +
						"              \"\\\"outParam\\\" : \\\"java.lang.String[]\\\"\" +" +
						"            \"}" + (i < methods.size() - 1 ? "," : "") + "\" +";
				infoStr += funcDef;
			}
			// infoStr += 
		}
		
//						 "            \"{\" +" +
//						 
//						 "            \"}\" +" +
						 
		infoStr	= "          \"]\" +\n" +
				  "        \"}\";\n" +
				  "      return json;";
		return infoStr;
	}
	
	/**
	 * generator of the client
	 * 
	 * @param env
	 * @param type
	 */
	public static void generateClient(ProcessingEnvironment env, TypeElement type) {
		// get service attributes
		// MobileService classService = type.getAnnotation(MobileService.class);
		// CommModel commModel = classService.commModel();
		// TransmitType transType = classService.transmitType();
		
		String fullClassName = type.getQualifiedName().toString();
		int lastDotIndex = fullClassName.lastIndexOf('.');
		String packageName = fullClassName.substring(0, lastDotIndex);
		String className = fullClassName.substring(lastDotIndex + 1);
		
		// define client class name
		String clientClassName = className + "Client";
		
		try {
			JavaFileObject builderFile = env.getFiler().createSourceFile(clientClassName);
			PrintWriter writer = new PrintWriter(builderFile.openWriter());

			// print package and default imports
			writer.println("package " + packageName + ";");
			writer.println();
			writer.println("import com.usu.tinyservice.messages.binary.InParam;");
			writer.println("import com.usu.tinyservice.messages.binary.RequestMessage;");
			writer.println("import com.usu.tinyservice.network.NetUtils;");
			writer.println("import com.usu.tinyservice.network.ReceiveListener;");
			writer.println("import com.usu.tinyservice.network.Requester;");
			writer.println();
			
			// declare class prototype 
			writer.println("public class " + clientClassName + " {");
			
			// declare the original service & network class 
			classInstance = className.toLowerCase();
			writer.println("  public ReceiveListener listener;"); 
			writer.println("  private RequesterX req;");
			writer.println();
			
			// define the server constructor
			writer.println("  public " + clientClassName + "(ReceiveListener listener) {");
			writer.println("    // start listener");
			writer.println("    this.listener = listener;\n");
			writer.println("    // create request message and send");
			writer.println("    req = new RequesterX();");
			writer.println("    req.start();");
			writer.println("  }");
			writer.println();
			
			// define the client function stubs
			// get list of inner methods
			List<? extends Element> methods = type.getEnclosedElements();
			// define all the function wrapper
			String clientFuncs = "";
			for (int i = 0; i < methods.size(); i++) {
				// clientFuncs += printFunctionCaller(transType, methods.get(i)) + "\n";
				clientFuncs += printFunctionCaller(methods.get(i)) + "\n";
			}
			writer.println(clientFuncs);
			
			// the last part
			writer.println("  class RequesterX extends Requester {\n" + 
						   "	@Override\n" + 
						   "	public void receive(byte[] resp) {\n" + 
						   "	  listener.dataReceived(resp);\n" + 
						   "	}\n" + 
						   "  }");
			writer.println("}");
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * generate each function declaration for the client 
	 * 
	 * @param e
	 * @return
	 */
	// private static String printFunctionCaller(TransmitType transType, Element e) {
	private static String printFunctionCaller(Element e) {		
		ServiceMethod sm = e.getAnnotation(ServiceMethod.class);
		ExecutableElement ee = (ExecutableElement) e;
		String funcName = ee.getSimpleName().toString();

		// define the total string of the function caller
		String funcCaller = "  public ";
		// String retType = getOnlyName(ee.getReturnType().toString());
		String retType = ee.getReturnType().toString();
		
		if (sm == null || !(e instanceof ExecutableElement)) {
			return "";
		}
		
		// prepare function prototype
		if (sm.syncMode() == SyncMode.Async) {
			funcCaller += "void " + funcName + "(";
		} else {
			funcCaller += retType + " " + funcName + "(";
		}
		
		// prepare the input parameters
		List<? extends VariableElement> ves = ee.getParameters();
		VariableElement ve;
		String inParamStr = ves.size() > 0 ? "    reqMsg.inParams = new InParam[" + ves.size() + "];\n" : "";
		
		String vType, vName;
		for (int i = 0; i < ves.size(); i++) {
			ve = ves.get(i);
			// vType = getOnlyName(ve.asType().toString());
			vType = ve.asType().toString();
			vName = ve.getSimpleName().toString();
			
			// funcCaller += getOnlyName(vType + " " + vName + (i < ves.size() - 1 ? ", " : ""));
			funcCaller += vType + " " + vName + (i < ves.size() - 1 ? ", " : "");
			
			// inParamStr += "    String[] param" + (i + 1) + " = NetUtils.getStringArray(" + vName + ");\n" +
			// inParamStr += "    reqMsg.inParams[" + i + "] = new InParam(\"" + vName + "\", \"" + vType + "\", param" + (i + 1) + ");\n";
			inParamStr += "    reqMsg.inParams[" + i + "] = new InParam(\"" + vName + "\", \"" + vType + "\", " + vName + ");\n";
		}
		
		funcCaller += ") {\n" +
					  "    // compose input parameters\n" +
					  "    String functionName = \"" + funcName + "\";\n" + 
					  "    String outType = \"" + retType + "\";\n" +
					  "    RequestMessage reqMsg = new RequestMessage(functionName, outType);\n" + 
					  "    \n" +
					  "    // create request message and send\n";
		
		// concatenate the input parameter analytic part
		funcCaller += inParamStr + "\n";
		
		// prepare the message to send to server
		funcCaller += "    // create a binary message\n" +
			  	  	  "    byte[] reqBytes = NetUtils.serialize(reqMsg);\n" + 
			  	  	  "    req.send(reqBytes);\n";
		
		// enclose part
		funcCaller += "  }\n";
		
		return funcCaller;
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
//		switch (type) {
//			case "byte": {
//				return "Byte.parseByte(" + valStr + ")";
//			}
//			case "char": {
//				return valStr + ".charAt[0]";
//			}
//			case "short": {
//				return "Short.parseShort(" + valStr + ")";
//			}
//			case "int": {
//				return "Integer.parseInt(" + valStr + ")";
//			}
//			case "long": {
//				return "Long.parseLong(" + valStr + ")";
//			}
//			case "float": {
//				return "Float.parseFloat(" + valStr + ")";
//			}
//			case "double": {
//				return "Double.parseDouble(" + valStr + ")";
//			}
//			case "String": {
//				return valStr;
//			}
//			case "boolean": {
//				return "Boolean.parseBoolean(" + valStr + ")";
//			}
//			default: {
//				return type;
//			}
//		}
		
		return "(" + type + ") " + valStr;
	}
	
//	/**
//	 * remove the package name from the full-name 
//	 * 
//	 * @param fullName
//	 * @return
//	 */
//	private static String getOnlyName(String fullName) {
//		int lastDot = fullName.lastIndexOf('.');
//		return fullName.substring(lastDot + 1);
//	}
}
