package soya.framework.tool.codegen;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

public abstract class JavaPoetBuilderCommand extends JavaCodegenCommand {

    @Override
    public String call() throws Exception {
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
