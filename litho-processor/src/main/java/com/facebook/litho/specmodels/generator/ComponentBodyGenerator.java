/*
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.litho.specmodels.generator;

import static com.facebook.litho.specmodels.generator.GeneratorConstants.PREVIOUS_RENDER_DATA_FIELD_NAME;
import static com.facebook.litho.specmodels.generator.GeneratorConstants.STATE_CONTAINER_FIELD_NAME;
import static com.facebook.litho.specmodels.model.ClassNames.COMPONENT;

import android.support.annotation.VisibleForTesting;
import com.facebook.litho.annotations.InjectProp;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.ResType;
import com.facebook.litho.annotations.State;
import com.facebook.litho.specmodels.internal.ImmutableList;
import com.facebook.litho.specmodels.model.ClassNames;
import com.facebook.litho.specmodels.model.EventDeclarationModel;
import com.facebook.litho.specmodels.model.EventMethod;
import com.facebook.litho.specmodels.model.InterStageInputParamModel;
import com.facebook.litho.specmodels.model.MethodParamModel;
import com.facebook.litho.specmodels.model.PropModel;
import com.facebook.litho.specmodels.model.RenderDataDiffModel;
import com.facebook.litho.specmodels.model.SpecMethodModel;
import com.facebook.litho.specmodels.model.SpecModel;
import com.facebook.litho.specmodels.model.SpecModelUtils;
import com.facebook.litho.specmodels.model.StateParamModel;
import com.facebook.litho.specmodels.model.TreePropModel;
import com.facebook.litho.specmodels.model.UpdateStateMethod;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;

/** Class that generates the implementation of a Component. */
public class ComponentBodyGenerator {

  private ComponentBodyGenerator() {}

  public static TypeSpecDataHolder generate(
      SpecModel specModel,
      @Nullable MethodParamModel optionalField) {
    final TypeSpecDataHolder.Builder builder = TypeSpecDataHolder.newBuilder();

    final boolean hasState = !specModel.getStateValues().isEmpty();
    final boolean needsRenderDataInfra = !specModel.getRenderDataDiffs().isEmpty();
    final ClassName stateContainerClass =
        ClassName.bestGuess(getStateContainerClassName(specModel));
    final ClassName previousRenderDataClass =
        ClassName.bestGuess(RenderDataGenerator.getRenderDataImplClassName(specModel));

    if (hasState) {
      builder.addField(stateContainerClass, STATE_CONTAINER_FIELD_NAME, Modifier.PRIVATE);
      builder.addMethod(generateStateContainerGetter(specModel.getStateContainerClass()));
    }

    if (needsRenderDataInfra) {
      builder.addField(previousRenderDataClass, PREVIOUS_RENDER_DATA_FIELD_NAME, Modifier.PRIVATE);
    }

    builder
        .addTypeSpecDataHolder(generateInjectedFields(specModel))
        .addTypeSpecDataHolder(generateProps(specModel))
        .addTypeSpecDataHolder(generateTreeProps(specModel))
        .addTypeSpecDataHolder(generateInterStageInputs(specModel))
        .addTypeSpecDataHolder(generateOptionalField(optionalField))
        .addTypeSpecDataHolder(generateEventHandlers(specModel))
        .addTypeSpecDataHolder(generateEventTriggers(specModel));

    builder.addMethod(generateGetSimpleName(specModel));
    builder.addMethod(generateIsEquivalentMethod(specModel));

    builder.addTypeSpecDataHolder(generateCopyInterStageImpl(specModel));
    builder.addTypeSpecDataHolder(generateOnUpdateStateMethods(specModel));
    builder.addTypeSpecDataHolder(generateMakeShallowCopy(specModel, hasState));

    if (hasState) {
      builder.addType(generateStateContainer(specModel));
    }
    if (needsRenderDataInfra) {
      builder.addType(generatePreviousRenderDataContainerImpl(specModel));
    }

    return builder.build();
  }

  private static TypeSpecDataHolder generateOptionalField(MethodParamModel optionalField) {
    final TypeSpecDataHolder.Builder typeSpecDataHolder = TypeSpecDataHolder.newBuilder();
    if (optionalField == null) {
      return typeSpecDataHolder.build();
    }

    typeSpecDataHolder.addField(
        FieldSpec.builder(optionalField.getType(), optionalField.getName()).build());
    return typeSpecDataHolder.build();
  }

  static TypeSpec generateStateContainer(SpecModel specModel) {
    final TypeSpec.Builder stateContainerClassBuilder =
        TypeSpec.classBuilder(getStateContainerClassName(specModel))
            .addSuperinterface(specModel.getStateContainerClass());

    if (!specModel.hasInjectedDependencies()) {
      stateContainerClassBuilder.addAnnotation(
          AnnotationSpec.builder(VisibleForTesting.class)
              .addMember("otherwise", "$L", VisibleForTesting.PRIVATE).build());
      stateContainerClassBuilder.addModifiers(Modifier.STATIC);
      stateContainerClassBuilder.addTypeVariables(specModel.getTypeVariables());
    }

    for (StateParamModel stateValue : specModel.getStateValues()) {
      stateContainerClassBuilder.addField(FieldSpec.builder(
          stateValue.getType(),
          stateValue.getName()).addAnnotation(State.class).build());
    }

    return stateContainerClassBuilder.build();
  }

  static TypeSpec generatePreviousRenderDataContainerImpl(SpecModel specModel) {
    final String className = RenderDataGenerator.getRenderDataImplClassName(specModel);
    final TypeSpec.Builder renderInfoClassBuilder =
        TypeSpec.classBuilder(className).addSuperinterface(ClassNames.RENDER_DATA);

    if (!specModel.hasInjectedDependencies()) {
      renderInfoClassBuilder.addModifiers(Modifier.STATIC, Modifier.PRIVATE);
      renderInfoClassBuilder.addTypeVariables(specModel.getTypeVariables());
    }

    final String copyParamName = "info";
    final String recordParamName = "component";
    final MethodSpec.Builder copyBuilder = MethodSpec.methodBuilder("copy")
        .addParameter(ClassName.bestGuess(className), copyParamName);
    final MethodSpec.Builder recordBuilder =
        MethodSpec.methodBuilder("record")
            .addParameter(specModel.getComponentTypeName(), recordParamName);

    for (RenderDataDiffModel diff : specModel.getRenderDataDiffs()) {
      final MethodParamModel modelToDiff =
          SpecModelUtils.getReferencedParamModelForDiff(specModel, diff);

      if (modelToDiff == null) {
        throw new RuntimeException(
            "Got Diff of a param that doesn't seem to exist: " + diff.getName() + ". This should " +
                "have been caught in the validation pass.");
      }

      if (!(modelToDiff instanceof PropModel || modelToDiff instanceof StateParamModel)) {
        throw new RuntimeException(
            "Got Diff of a param that is not a @Prop or @State! (" + diff.getName() + ", a " +
                modelToDiff.getClass().getSimpleName() + "). This should have been caught in the " +
                "validation pass.");
      }

      final String name = modelToDiff.getName();
      if (modelToDiff instanceof PropModel) {
        renderInfoClassBuilder.addField(FieldSpec.builder(
            modelToDiff.getType(),
            name).addAnnotation(Prop.class).build());
      } else {
        renderInfoClassBuilder.addField(FieldSpec.builder(
            modelToDiff.getType(),
            modelToDiff.getName()).addAnnotation(State.class).build());
      }

      copyBuilder.addStatement("$L = $L.$L", name, copyParamName, name);
      recordBuilder.addStatement(
          "$L = $L.$L",
          name,
          recordParamName,
          getImplAccessor(specModel, modelToDiff));
    }

    return renderInfoClassBuilder
        .addMethod(copyBuilder.build())
        .addMethod(recordBuilder.build())
        .build();
  }

  static String getInstanceRefName(SpecModel specModel) {
    final String refClassName = specModel.getComponentName() + "Ref";
    return refClassName.substring(0, 1).toLowerCase(Locale.ROOT) + refClassName.substring(1);
  }

  static String getStateContainerClassName(SpecModel specModel) {
    if (specModel.getStateValues().isEmpty()) {
      return specModel.getStateContainerClass().toString();
    } else {
      return specModel.getComponentName() + GeneratorConstants.STATE_CONTAINER_NAME_SUFFIX;
    }
  }

  static MethodSpec generateStateContainerGetter(TypeName stateContainerClassName) {
    return MethodSpec.methodBuilder("getStateContainer")
        .addModifiers(Modifier.PROTECTED)
        .addAnnotation(Override.class)
        .returns(stateContainerClassName)
        .addStatement("return $N", STATE_CONTAINER_FIELD_NAME)
        .build();
  }

  static TypeSpecDataHolder generateProps(SpecModel specModel) {
    final TypeSpecDataHolder.Builder typeSpecDataHolder = TypeSpecDataHolder.newBuilder();
    final ImmutableList<PropModel> props = specModel.getProps();

    for (PropModel prop : props) {
      final FieldSpec.Builder fieldBuilder = FieldSpec.builder(prop.getType(), prop.getName())
          .addAnnotation(
              AnnotationSpec.builder(Prop.class)
                  .addMember("resType", "$T.$L", ResType.class, prop.getResType())
                  .addMember("optional", "$L", prop.isOptional())
                  .build());
      if (prop.hasDefault(specModel.getPropDefaults())) {
        fieldBuilder.initializer("$L.$L", specModel.getSpecName(), prop.getName());
      }

      typeSpecDataHolder.addField(fieldBuilder.build());
    }

    return typeSpecDataHolder.build();
  }

  static TypeSpecDataHolder generateInjectedFields(SpecModel specModel) {
    final TypeSpecDataHolder.Builder typeSpecDataHolder = TypeSpecDataHolder.newBuilder();

    if (specModel.hasInjectedDependencies()) {
      final Set<MethodParamModel> injectedParams = extractInjectedParams(specModel);
      typeSpecDataHolder.addTypeSpecDataHolder(
          specModel.getDependencyInjectionHelper().generateInjectedFields(injectedParams));
    }

    return typeSpecDataHolder.build();
  }

  static TypeSpecDataHolder generateTreeProps(SpecModel specModel) {
    final TypeSpecDataHolder.Builder typeSpecDataHolder = TypeSpecDataHolder.newBuilder();
    final ImmutableList<TreePropModel> treeProps = specModel.getTreeProps();

    for (TreePropModel treeProp : treeProps) {
      typeSpecDataHolder.addField(
          FieldSpec.builder(treeProp.getType(), treeProp.getName()).build());
    }

    return typeSpecDataHolder.build();
  }

  static TypeSpecDataHolder generateInterStageInputs(SpecModel specModel) {
    final TypeSpecDataHolder.Builder typeSpecDataHolder = TypeSpecDataHolder.newBuilder();
    final ImmutableList<InterStageInputParamModel> interStageInputs =
        specModel.getInterStageInputs();

    for (InterStageInputParamModel interStageInput : interStageInputs) {
      typeSpecDataHolder.addField(
          FieldSpec.builder(interStageInput.getType(), interStageInput.getName()).build());
    }

    return typeSpecDataHolder.build();
  }

  static TypeSpecDataHolder generateEventHandlers(SpecModel specModel) {
    final TypeSpecDataHolder.Builder typeSpecDataHolder = TypeSpecDataHolder.newBuilder();

    for (EventDeclarationModel eventDeclaration : specModel.getEventDeclarations()) {
      typeSpecDataHolder.addField(
          FieldSpec.builder(
              ClassNames.EVENT_HANDLER,
              getEventHandlerInstanceName(eventDeclaration.name))
              .build());
    }

    return typeSpecDataHolder.build();
  }

  static String getEventHandlerInstanceName(ClassName eventHandlerClassName) {
    final String eventHandlerName = eventHandlerClassName.simpleName();
    return eventHandlerName.substring(0, 1).toLowerCase(Locale.ROOT) +
        eventHandlerName.substring(1) +
        "Handler";
  }

  static TypeSpecDataHolder generateEventTriggers(SpecModel specModel) {
    final TypeSpecDataHolder.Builder typeSpecDataHolder = TypeSpecDataHolder.newBuilder();

    for (SpecMethodModel<EventMethod, EventDeclarationModel> eventMethodModel :
        specModel.getTriggerMethods()) {
      typeSpecDataHolder.addField(
          FieldSpec.builder(
                  ClassNames.EVENT_TRIGGER, getEventTriggerInstanceName(eventMethodModel.name))
              .build());
    }

    return typeSpecDataHolder.build();
  }

  static String getEventTriggerInstanceName(CharSequence eventTriggerClassName) {
    final String eventTriggerName = eventTriggerClassName.toString();

    return eventTriggerName.substring(0, 1).toLowerCase(Locale.ROOT)
        + eventTriggerName.substring(1)
        + "Trigger";
  }

  static MethodSpec generateGetSimpleName(SpecModel specModel) {
    return MethodSpec.methodBuilder("getSimpleName")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .returns(ClassNames.STRING)
        .addStatement("return \"$N\"", specModel.getComponentName())
        .build();
  }

  static MethodSpec generateIsEquivalentMethod(SpecModel specModel) {
    final String className = specModel.getComponentName();
    final String instanceRefName = getInstanceRefName(specModel);

    MethodSpec.Builder isEquivalentBuilder =
        MethodSpec.methodBuilder("isEquivalentTo")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.BOOLEAN)
            .addParameter(specModel.getComponentClass(), "other")
            .beginControlFlow("if (this == other)")
            .addStatement("return true")
            .endControlFlow()
            .beginControlFlow("if (other == null || getClass() != other.getClass())")
            .addStatement("return false")
            .endControlFlow()
            .addStatement("$N $N = ($N) other", className, instanceRefName, className);

    if (specModel.shouldCheckIdInIsEquivalentToMethod()) {
      isEquivalentBuilder
          .beginControlFlow("if (this.getId() == $N.getId())", instanceRefName)
          .addStatement("return true")
          .endControlFlow();
    }

    for (PropModel prop : specModel.getProps()) {
      isEquivalentBuilder.addCode(getCompareStatement(specModel, instanceRefName, prop));
    }

    for (StateParamModel state : specModel.getStateValues()) {
      isEquivalentBuilder.addCode(getCompareStatement(specModel, instanceRefName, state));
    }

    for (TreePropModel treeProp : specModel.getTreeProps()) {
      isEquivalentBuilder.addCode(getCompareStatement(specModel, instanceRefName, treeProp));
    }

    isEquivalentBuilder.addStatement("return true");

    return isEquivalentBuilder.build();
  }

  static TypeSpecDataHolder generateCopyInterStageImpl(SpecModel specModel) {
    final TypeSpecDataHolder.Builder typeSpecDataHolder = TypeSpecDataHolder.newBuilder();
    final ImmutableList<InterStageInputParamModel> interStageInputs =
        specModel.getInterStageInputs();

    if (!interStageInputs.isEmpty()) {
      final String className = specModel.getComponentName();
      final String instanceName = getInstanceRefName(specModel);
      final MethodSpec.Builder copyInterStageComponentBuilder =
          MethodSpec.methodBuilder("copyInterStageImpl")
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PROTECTED)
              .returns(TypeName.VOID)
              .addParameter(ClassNames.COMPONENT, "component")
              .addStatement(
                  "$N $N = ($N) component",
                  className,
                  instanceName,
                  className);

      for (InterStageInputParamModel interStageInput : interStageInputs) {
        copyInterStageComponentBuilder
            .addStatement(
                "$N = $N.$N",
                interStageInput.getName(),
                instanceName,
                interStageInput.getName());
      }

      typeSpecDataHolder.addMethod(copyInterStageComponentBuilder.build());
    }

    return typeSpecDataHolder.build();
  }

  static TypeSpecDataHolder generateOnUpdateStateMethods(SpecModel specModel) {
    TypeSpecDataHolder.Builder typeSpecDataHolder = TypeSpecDataHolder.newBuilder();

    for (SpecMethodModel<UpdateStateMethod, Void> updateStateMethodModel :
        specModel.getUpdateStateMethods()) {
      final String stateUpdateClassName = getStateUpdateClassName(updateStateMethodModel);
      final List<MethodParamModel> params = getParams(updateStateMethodModel);

      final MethodSpec.Builder methodSpecBuilder = MethodSpec
          .methodBuilder("create" + stateUpdateClassName)
          .addModifiers(Modifier.PRIVATE)
          .returns(ClassName.bestGuess(stateUpdateClassName));

      for (MethodParamModel param : params) {
        methodSpecBuilder
            .addParameter(ParameterSpec.builder(param.getType(), param.getName()).build());
      }

      final CodeBlock.Builder constructor = CodeBlock.builder();
      constructor.add("return new $N(", stateUpdateClassName);

      for (int i = 0, size = params.size(); i < size; i++) {
        constructor.add(params.get(i).getName());
        if (i < params.size() - 1) {
          constructor.add(", ");
        }
      }
      constructor.add(");\n");

      methodSpecBuilder.addCode(constructor.build());
      typeSpecDataHolder.addMethod(methodSpecBuilder.build());
    }

    return typeSpecDataHolder.build();
  }

  static TypeSpecDataHolder generateMakeShallowCopy(SpecModel specModel, boolean hasState) {
    TypeSpecDataHolder.Builder typeSpecDataHolder = TypeSpecDataHolder.newBuilder();

    final List<MethodParamModel> componentsInImpl = findComponentsInImpl(specModel);
    final ImmutableList<InterStageInputParamModel> interStageComponentVariables =
        specModel.getInterStageInputs();
    final ImmutableList<SpecMethodModel<UpdateStateMethod, Void>> updateStateMethodModels =
        specModel.getUpdateStateMethods();
    final boolean hasDeepCopy = specModel.hasDeepCopy();

    if (componentsInImpl.isEmpty() &&
        interStageComponentVariables.isEmpty() &&
        updateStateMethodModels.isEmpty()) {
      return typeSpecDataHolder.build();
    }

    final String className = specModel.getComponentName();
    MethodSpec.Builder builder =
        MethodSpec.methodBuilder("makeShallowCopy")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(ClassName.bestGuess(className));

    String deepCopy = hasDeepCopy ? "deepCopy" : "";

    if (hasDeepCopy) {
      builder.addParameter(ParameterSpec.builder(TypeName.BOOLEAN, "deepCopy").build());
    }

    builder.addStatement(
        "$L $L = ($L) super.makeShallowCopy($L)", className, "component", className, deepCopy);

    for (MethodParamModel componentParam : componentsInImpl) {
      builder.addStatement(
          "component.$L = component.$L != null ? component.$L.makeShallowCopy() : null",
          componentParam.getName(),
          componentParam.getName(),
          componentParam.getName());
    }

    if (hasDeepCopy) {
      builder.beginControlFlow("if (!deepCopy)");
    }

    for (InterStageInputParamModel interStageInput : specModel.getInterStageInputs()) {
      builder.addStatement("component.$L = null", interStageInput.getName());
    }

    final String stateContainerClassName = getStateContainerClassName(specModel);
    if (stateContainerClassName != null && hasState) {
      builder.addStatement(
          "component.$N = new $T()",
          STATE_CONTAINER_FIELD_NAME,
          ClassName.bestGuess(stateContainerClassName));
    }

    if (hasDeepCopy) {
      builder.endControlFlow();
    }

    builder.addStatement("return component");

    return typeSpecDataHolder.addMethod(builder.build()).build();
  }

  private static List<MethodParamModel> findComponentsInImpl(SpecModel specModel) {
    final List<MethodParamModel> componentsInImpl = new ArrayList<>();

    for (PropModel prop : specModel.getProps()) {
      final TypeName typeName = prop.getType();
      if (typeName.equals(ClassNames.COMPONENT) ||
          (typeName instanceof ParameterizedTypeName &&
              ((ParameterizedTypeName) typeName).rawType.equals(COMPONENT))) {
        componentsInImpl.add(prop);
      }
    }

    return componentsInImpl;
  }

  private static List<MethodParamModel> getParams(
      SpecMethodModel<UpdateStateMethod, Void> updateStateMethodModel) {
    final List<MethodParamModel> params = new ArrayList<>();
    for (MethodParamModel methodParamModel : updateStateMethodModel.methodParams) {
      for (Annotation annotation : methodParamModel.getAnnotations()) {
        if (annotation.annotationType().equals(Param.class)) {
          params.add(methodParamModel);
          break;
        }
      }
    }

    return params;
  }

  private static String getStateUpdateClassName(
      SpecMethodModel<UpdateStateMethod, Void> updateStateMethodModel) {
    String methodName = updateStateMethodModel.name.toString();
    return methodName.substring(0, 1).toUpperCase(Locale.ROOT)
        + methodName.substring(1)
        + GeneratorConstants.STATE_UPDATE_NAME_SUFFIX;
  }

  private static CodeBlock getCompareStatement(
      SpecModel specModel,
      String implInstanceName,
      MethodParamModel field) {
    final CodeBlock.Builder codeBlock = CodeBlock.builder();

    final String implAccessor = getImplAccessor(specModel, field);
    if (field.getType() == TypeName.FLOAT) {
      codeBlock
          .beginControlFlow(
              "if (Float.compare($L, $L.$L) != 0)",
              implAccessor,
              implInstanceName,
              implAccessor)
          .addStatement("return false")
          .endControlFlow();
    } else if (field.getType() == TypeName.DOUBLE) {
      codeBlock
          .beginControlFlow(
              "if (Double.compare($L, $L.$L) != 0)",
              implAccessor,
              implInstanceName,
              implAccessor)
          .addStatement("return false")
          .endControlFlow();
    } else if (field.getType() instanceof ArrayTypeName) {
      codeBlock
          .beginControlFlow(
              "if (!$T.equals($L, $L.$L))",
              Arrays.class,
              implAccessor,
              implInstanceName,
              implAccessor)
          .addStatement("return false")
          .endControlFlow();
    } else if (field.getType().isPrimitive()) {
      codeBlock
          .beginControlFlow(
              "if ($L != $L.$L)",
              implAccessor,
              implInstanceName,
              implAccessor)
          .addStatement("return false")
          .endControlFlow();
    } else if (field.getType().equals(ClassNames.REFERENCE)) {
      codeBlock
          .beginControlFlow(
              "if (Reference.shouldUpdate($L != $L.$L))",
              implAccessor,
              implInstanceName,
              implAccessor)
          .addStatement("return false")
          .endControlFlow();
    } else {
      final String equalMethodName =
          shouldUseIsEquivalentTo(specModel, field) ? "isEquivalentTo" : "equals";

      codeBlock
          .beginControlFlow(
              "if ($L != null ? !$L.$L($L.$L) : $L.$L != null)",
              implAccessor,
              implAccessor,
              equalMethodName,
              implInstanceName,
              implAccessor,
              implInstanceName,
              implAccessor)
          .addStatement("return false")
          .endControlFlow();
    }

    return codeBlock.build();
  }

  private static boolean shouldUseIsEquivalentTo(SpecModel specModel, MethodParamModel field) {
    return (field.getType().equals(ClassNames.COMPONENT)
        || field.getType().equals(specModel.getComponentClass()));
  }

  static String getImplAccessor(SpecModel specModel, MethodParamModel methodParamModel) {
    if (methodParamModel instanceof StateParamModel ||
        SpecModelUtils.getStateValueWithName(specModel, methodParamModel.getName()) != null) {
      return STATE_CONTAINER_FIELD_NAME + "." + methodParamModel.getName();
    }

    return methodParamModel.getName();
  }

  public static Set<MethodParamModel> extractInjectedParams(SpecModel specModel) {
    final Set<MethodParamModel> injectedParams = new LinkedHashSet<>();

    for (SpecMethodModel delegateMethod : specModel.getDelegateMethods()) {
      for (MethodParamModel param : ((List<MethodParamModel>) delegateMethod.methodParams)) {
        if (SpecModelUtils.hasAnnotation(param, InjectProp.class)) {
          injectedParams.add(param);
        }
      }
    }

    for (SpecMethodModel eventMethod : specModel.getEventMethods()) {
      for (MethodParamModel param : ((List<MethodParamModel>) eventMethod.methodParams)) {
        if (SpecModelUtils.hasAnnotation(param, InjectProp.class)) {
          injectedParams.add(param);
        }
      }
    }

    return injectedParams;
  }
}
