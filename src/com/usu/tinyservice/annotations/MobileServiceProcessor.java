package com.usu.tinyservice.annotations;

import com.google.auto.service.AutoService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import com.google.common.collect.ImmutableList;

/**
 * Created by lee on 9/20/17.
 */
@AutoService(Processor.class)
public class MobileServiceProcessor extends AbstractProcessor {
    private Messager messager = null;
//    private Filer filer = null;

    //The processor has to have an empty constructor
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment){
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
//        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        //get all elements annotated with StatusInfo
        // Collection<? extends Element> annotatedElements = env.getElementsAnnotatedWith(MobileService.class);
    	Collection<? extends Element> annotatedElements = env.getElementsAnnotatedWith(MobileService.class);

        //filter out elements we don't need
        List<TypeElement> types = new ImmutableList.Builder<TypeElement>().addAll(
                                    ElementFilter.typesIn(annotatedElements)).build();

        for (TypeElement type : types) {
            //interfaces are types too, but we only need classes
            //we need to check if the TypeElement is a valid class
            if (isValidClass(type, MobileService.class.getName())) {
                // writeSourceFile(type);
            	// MobileServiceCreator.generateServer(processingEnv, type);
            	// MobileServiceCreator.generateClient(processingEnv, type);
            	
            	// MobileServiceBinCreator.generateServer(processingEnv, type);
            	// MobileServiceBinCreator.generateClient(processingEnv, type);
            	
            	MobileServiceMultiCreator.generateWorker(processingEnv, type);
            	MobileServiceMultiCreator.generateClient(processingEnv, type);
            } else {
                return true;
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(MobileService.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private boolean isValidClass(TypeElement type, String className){
        if(type.getKind() != ElementKind.CLASS){
            messager.printMessage(Diagnostic.Kind.ERROR, type.getSimpleName() + 
            					" only classes can be annotated with " + className);
            return false;
        }

        if(type.getModifiers().contains(Modifier.PRIVATE)){
            messager.printMessage(Diagnostic.Kind.ERROR, type.getSimpleName() + 
            					" only public classes can be annotated with " + className);
            return false;
        }

        if(type.getModifiers().contains(Modifier.ABSTRACT)){
            messager.printMessage(Diagnostic.Kind.ERROR, type.getSimpleName() + 
            					" only non abstract classes can be annotated with " + className);
            return false;
        }

        return true;
    }

//    private void writeSourceFile(TypeElement originatingType) {
//        //get Log class from android.util package
//        //This will make sure the Log class is properly imported into our class
//        ClassName logClassName = ClassName.get("android.util", "Log");
//
//        //get the current annotated class name
//        TypeVariableName typeVariableName = TypeVariableName.get(originatingType.getSimpleName().toString());
//
//        //create static void method named log
//        MethodSpec log = MethodSpec.methodBuilder(METHOD_LOG)
//                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                .returns(void.class)
//                //Parameter variable based on the annotated class
//                .addParameter(typeVariableName, KEY_PARAM_NAME)
//                //add a Lod.d("ClassName", String.format(class fields));
//                .addStatement("$T.d($S, $L)", logClassName, originatingType.getSimpleName().toString(), generateFormater(originatingType))
//                .build();
//
//        //create a class to wrap our method
//        //the class name will be the annotated class name + _Log
//        TypeSpec loggerClass = TypeSpec.classBuilder(originatingType.getSimpleName().toString() + CLASS_SUFFIX)
//                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//                //add the log statetemnt from above
//                .addMethod(log)
//                .build();
//        //create the file
//        JavaFile javaFile = JavaFile.builder(originatingType.getEnclosingElement().toString(), loggerClass)
//                .build();
//
//        try {
//            javaFile.writeTo(filer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    
//    private String generateFormater(TypeElement originatingType) {
//        List<VariableElement> fields = new ImmutableList.Builder<VariableElement>().
//        					addAll(ElementFilter.fieldsIn(originatingType.getEnclosedElements())).build();
//        String sformat = "String.format(\"";
//
//        for (VariableElement e : fields) {
//            sformat += e.getSimpleName() + " - %s ";
//        }
//        sformat += "\"";
//
//        for (VariableElement f : fields) {
//            sformat += ", " + KEY_PARAM_NAME + "." + f.getSimpleName();
//        }
//
//        sformat += ")";
//
//        return sformat;
//    }
}
