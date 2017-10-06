package com.usu.tinyservice.annotations;

import java.awt.Window.Type;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

/**
 * create 
 * @author minhld
 *
 */
public class MobileServiceCreator {
	public static void generateServer(ProcessingEnvironment env, TypeElement type) {
		// get service attributes
		MobileService classService = type.getAnnotation(MobileService.class);
		CommModel commModel = classService.commModel();
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
			String classInstance = className.toLowerCase();
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
	
	private static void printBinaryResponder(PrintWriter writer, Element e, List<? extends Element> methods) {
		// define all the function wrapper
		for (int i = 0; i < methods.size(); i++) {
			printSyncServerHandler(writer, methods.get(i));
		}
	}

	private static void printSyncServerHandler(PrintWriter writer, Element e) {
		if (e.getAnnotation(ServiceMethod.class) != null && e instanceof ExecutableElement) {
			
		}
	}
	

	private static void printJSONResponder(PrintWriter writer, Element e, List<? extends Element> methods) {
		writer.println("      String reqJSON = new String(req);");
		writer.println("      RequestMessage reqMsg = JSONHelper.getRequest(reqJSON);");
		writer.println();
		
		// define the switch - where all the functions are iterated here
		writer.println("      switch (reqMsg.functionName) {");

		// define all the function wrapper
		for (int i = 0; i < methods.size(); i++) {
			printAsyncServerHandler(writer, methods.get(i));
		}
		
		// close the part
		writer.println("      }");
	}
	
	private static void printAsyncServerHandler(PrintWriter writer, Element e) {
		ServiceMethod sm = e.getAnnotation(ServiceMethod.class);
		// only accept functions having annotation, function and sync_mode is ASYNC
		if (e.getAnnotation(ServiceMethod.class) != null && e instanceof ExecutableElement && sm.syncMode() == SyncMode.Async) {
			ExecutableElement ee = (ExecutableElement) e;
			
			writer.println("      case \"" + ee.getSimpleName() + "\": {");
			
			List<? extends VariableElement> ves = ee.getParameters();
			TypeMirror retType = ee.getReturnType();
			if (ves.size() > 0) {
				
			}
			
			writer.println("      }");
		}
	}
	
	public static void generateClient(TypeElement type) {
		
	}
}
