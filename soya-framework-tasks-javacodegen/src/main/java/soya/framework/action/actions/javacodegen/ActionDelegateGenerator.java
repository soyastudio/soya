package soya.framework.action.actions.javacodegen;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;


public class ActionDelegateGenerator extends JavaPoetBuilderAction {

    protected void defineClass(TypeSpec.Builder typeBuilder) {

    }

    protected void defineFields(TypeSpec.Builder typeBuilder) {

    }

    protected void defineMethods(TypeSpec.Builder typeBuilder) {
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        typeBuilder.addMethod(main);
    }
}
