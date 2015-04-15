package com.wordnik.swagger.codegen.languages;

import com.wordnik.swagger.codegen.*;
import com.wordnik.swagger.models.properties.*;

import java.io.File;
import java.util.*;

public class TypeScriptCodegen extends DefaultCodegen implements CodegenConfig {
  protected String invokerPackage = "io.swagger.client";
  protected String groupId = "io.swagger";
  protected String artifactId = "swagger-client";
  protected String artifactVersion = "1.0.0";

  ArrayList uniqueEnums = new ArrayList<CodegenProperty>();
  Set<String> uniqueEnumNames = new HashSet<String>();


  public CodegenType getTag() {
    return CodegenType.CLIENT;
  }

  public String getName() {
    return "typescript";
  }

  public String getHelp() {
    return "Generates a typescript client library.";
  }

  public TypeScriptCodegen() {
    super();
    outputFolder = "webapp_src/scripts/autogen";
    modelTemplateFiles.put("model.mustache", ".ts");
    apiTemplateFiles.put("api.mustache", ".ts");
    templateDir = "typescript";
    apiPackage = "backend";
    modelPackage = "backend";

    reservedWords = new HashSet<String> (
      Arrays.asList(
        "abstract", "continue", "for", "new", "switch", "assert",
        "default", "if", "package", "synchronized", "boolean", "do", "goto", "private",
        "this", "break", "double", "implements", "protected", "throw", "byte", "else",
        "import", "public", "throws", "case", "enum", "instanceof", "return", "transient",
        "catch", "extends", "int", "short", "try", "char", "final", "interface", "static",
        "void", "class", "finally", "long", "strictfp", "volatile", "const", "float",
        "native", "super", "while")
    );

    additionalProperties.put("uniqueEnums", uniqueEnums);
    supportingFiles.add(new SupportingFile("enums.mustache", "backend/models", "enums.ts"));

    additionalProperties.put("invokerPackage", invokerPackage);
    additionalProperties.put("groupId", groupId);
    additionalProperties.put("artifactId", artifactId);
    additionalProperties.put("artifactVersion", artifactVersion);

      typeMapping = new HashMap<String, String>();
      typeMapping.put("int", "number");
      typeMapping.put("integer", "number");
      typeMapping.put("long", "number");
      typeMapping.put("Long", "number");
      typeMapping.put("float", "number");
      typeMapping.put("byte", "number");
      typeMapping.put("short", "number");
      typeMapping.put("double", "number");
      typeMapping.put("Number", "number");


      typeMapping.put("enum", "string");
      typeMapping.put("string", "string");
      typeMapping.put("String", "string");

      typeMapping.put("array", "List");
      typeMapping.put("set", "Set");

      typeMapping.put("boolean", "boolean");
      typeMapping.put("Boolean", "boolean");

      typeMapping.put("char", "Char");

      typeMapping.put("DateTime", "Date");




      //typeMapping.put("object", "Any");
      //typeMapping.put("file", "File");

      languageSpecificPrimitives = new HashSet<String>(
      Arrays.asList(
        "number", "string", "boolean"
      ));

    instantiationTypes.put("HashMap", "{ [index: string]: SOMECLASS; }");
    instantiationTypes.put("array", "ArrayList");
    instantiationTypes.put("map", "HashMap");

  }

  @Override
  public String escapeReservedWord(String name) {
    return "_" + name;
  }

  @Override
  public String apiFileFolder() {
    return outputFolder + "/"  + "/" + apiPackage().replace('.', File.separatorChar)+"/api";
  }

  public String modelFileFolder() {
    return outputFolder + "/"  + "/" + modelPackage().replace('.', File.separatorChar)+"/model";
  }

  @Override
  public String getTypeDeclaration(Property p) {
    System.out.println(p.toString());
    if(p instanceof ArrayProperty) {
      ArrayProperty ap = (ArrayProperty) p;
      Property inner = ap.getItems();
      return getTypeDeclaration(inner) + "[]";
      //return getSwaggerType(p) + "<" + getTypeDeclaration(inner) + ">";
    }
    else if (p instanceof MapProperty) {
      MapProperty mp = (MapProperty) p;
      Property inner = mp.getAdditionalProperties();
      return getSwaggerType(p) + "<String, " + getTypeDeclaration(inner) + ">";
    }
    //else if (p instanceof Model){
    //  return modelPackage+"."+super.getTypeDeclaration(p);
    //}
    return super.getTypeDeclaration(p);
  }

  @Override
  public String getSwaggerType(Property p) {
    String swaggerType = super.getSwaggerType(p);
    String type = null;
    if(typeMapping.containsKey(swaggerType)) {
      type = typeMapping.get(swaggerType);
      if(languageSpecificPrimitives.contains(type))
        return toModelName(type);
    }
    else
      type = swaggerType;
    return toModelName(type);
  }

  @Override
  public Map<String, Object> postProcessModels(Map<String, Object> objs) {
   for(Object o_list : (List)objs.get("models")){
       for(Object o : ((HashMap)o_list).values()) {
       CodegenModel model = (CodegenModel)o;
         for (CodegenProperty prop : model.vars) {
           if (prop.isEnum && !uniqueEnumNames.contains(prop.datatypeWithEnum)) {
             uniqueEnums.add(prop);
             uniqueEnumNames.add(prop.datatypeWithEnum);
           }
         }
     }
   }


    return super.postProcessModels(objs);
  }

}