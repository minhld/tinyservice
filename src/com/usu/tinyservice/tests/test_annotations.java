package com.usu.tinyservice.tests;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import com.usu.tinyservice.annotations.MobileServiceProcessor;

public class test_annotations extends Thread {
	public void run() {
		try {
			String source = "src";
			Iterable<JavaFileObject> files = getSourceFiles(source);
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			CompilationTask task = compiler.getTask(new PrintWriter(System.out), null, null, null, null, files);
			task.setProcessors(Arrays.asList(new MobileServiceProcessor()));
			task.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Iterable<JavaFileObject> getSourceFiles(String p_path) throws Exception {
	     JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	     StandardJavaFileManager files = compiler.getStandardFileManager(null, null, null);

	     files.setLocation(StandardLocation.SOURCE_PATH, Arrays.asList(new File(p_path)));

	     Set<Kind> fileKinds = Collections.singleton(Kind.SOURCE);
	     return files.list(StandardLocation.SOURCE_PATH, "", fileKinds, true);
	   }
	
	public static void main(String args[]) {
		new test_annotations().start();
	}
}
