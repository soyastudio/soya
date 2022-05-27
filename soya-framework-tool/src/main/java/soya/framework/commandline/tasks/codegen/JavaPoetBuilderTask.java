package soya.framework.commandline.tasks.codegen;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

public abstract class JavaPoetBuilderTask extends JavaCodegenTask {

    @Override
    public String execute() throws Exception {
        StringBuilder builder = new StringBuilder();
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        // define class:
        defineClass(typeBuilder);

        // define fields:
        defineFields(typeBuilder);

        // define methods:
        defineMethods(typeBuilder);

        JavaFile javaFile = JavaFile.builder(packageName, typeBuilder.build())
                .skipJavaLangImports(true)
                .build();
        javaFile.writeTo(builder);

        return builder.toString();
    }

    protected abstract void defineClass(TypeSpec.Builder typeBuilder);

    protected abstract void defineFields(TypeSpec.Builder typeBuilder);

    protected abstract void defineMethods(TypeSpec.Builder typeBuilder);
}
