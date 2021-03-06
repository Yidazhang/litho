/*
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.litho.specmodels.model;

import com.facebook.litho.specmodels.internal.ImmutableList;
import com.facebook.litho.specmodels.internal.RunMode;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import java.util.List;

/**
 * Model that is an abstract representation of a {@link com.facebook.litho.annotations.MountSpec}.
 */
public class MountSpecModel implements SpecModel, HasPureRender {
  private final SpecModelImpl mSpecModel;
  private final boolean mIsPureRender;
  private final boolean mCanMountIncrementally;
  private final boolean mShouldUseDisplayList;
  private final int mPoolSize;
  private final boolean mCanPreallocate;
  private final TypeName mMountType;
  private final SpecGenerator<MountSpecModel> mMountSpecGenerator;

  public MountSpecModel(
      String qualifiedSpecClassName,
      String componentClassName,
      ImmutableList<SpecMethodModel<DelegateMethod, Void>> delegateMethods,
      ImmutableList<SpecMethodModel<EventMethod, EventDeclarationModel>> eventMethods,
      ImmutableList<SpecMethodModel<EventMethod, EventDeclarationModel>> triggerMethods,
      ImmutableList<SpecMethodModel<UpdateStateMethod, Void>> updateStateMethods,
      ImmutableList<String> cachedPropNames,
      ImmutableList<TypeVariableName> typeVariables,
      ImmutableList<PropDefaultModel> propDefaults,
      ImmutableList<EventDeclarationModel> eventDeclarations,
      String classJavadoc,
      ImmutableList<AnnotationSpec> classAnnotations,
      ImmutableList<TagModel> tags,
      ImmutableList<PropJavadocModel> propJavadocs,
      boolean isPublic,
      DependencyInjectionHelper dependencyInjectionHelper,
      boolean isPureRender,
      boolean canMountIncrementally,
      boolean shouldUseDisplayList,
      int poolSize,
      boolean canPreallocate,
      TypeName mountType,
      SpecElementType specElementType,
      Object representedObject,
      SpecGenerator<MountSpecModel> mountSpecGenerator) {
    mSpecModel =
        SpecModelImpl.newBuilder()
            .qualifiedSpecClassName(qualifiedSpecClassName)
            .componentClassName(componentClassName)
            .componentClass(ClassNames.COMPONENT)
            .delegateMethods(delegateMethods)
            .eventMethods(eventMethods)
            .triggerMethods(triggerMethods)
            .updateStateMethods(updateStateMethods)
            .cachedPropNames(cachedPropNames)
            .typeVariables(typeVariables)
            .propDefaults(propDefaults)
            .eventDeclarations(eventDeclarations)
            .classAnnotations(classAnnotations)
            .tags(tags)
            .classJavadoc(classJavadoc)
            .propJavadocs(propJavadocs)
            .isPublic(isPublic)
            .dependencyInjectionGenerator(dependencyInjectionHelper)
            .specElementType(specElementType)
            .representedObject(representedObject)
            .build();
    mIsPureRender = isPureRender;
    mCanMountIncrementally = canMountIncrementally;
    mShouldUseDisplayList = shouldUseDisplayList;
    mPoolSize = poolSize;
    mCanPreallocate = canPreallocate;
    mMountType = mountType;
    mMountSpecGenerator = mountSpecGenerator;
  }

  @Override
  public String getSpecName() {
    return mSpecModel.getSpecName();
  }

  @Override
  public TypeName getSpecTypeName() {
    return mSpecModel.getSpecTypeName();
  }

  @Override
  public String getComponentName() {
    return mSpecModel.getComponentName();
  }

  @Override
  public TypeName getComponentTypeName() {
    return mSpecModel.getComponentTypeName();
  }

  @Override
  public ImmutableList<SpecMethodModel<DelegateMethod, Void>> getDelegateMethods() {
    return mSpecModel.getDelegateMethods();
  }

  @Override
  public ImmutableList<SpecMethodModel<EventMethod, EventDeclarationModel>> getEventMethods() {
    return mSpecModel.getEventMethods();
  }

  @Override
  public ImmutableList<SpecMethodModel<EventMethod, EventDeclarationModel>> getTriggerMethods() {
    return mSpecModel.getTriggerMethods();
  }

  @Override
  public ImmutableList<SpecMethodModel<UpdateStateMethod, Void>> getUpdateStateMethods() {
    return mSpecModel.getUpdateStateMethods();
  }

  /**
   * @return the list of props without taking deduplication or name cache adjustments into account.
   */
  @Override
  public ImmutableList<PropModel> getRawProps() {
    return mSpecModel.getRawProps();
  }

  @Override
  public ImmutableList<PropModel> getProps() {
    return mSpecModel.getProps();
  }

  @Override
  public ImmutableList<PropDefaultModel> getPropDefaults() {
    return mSpecModel.getPropDefaults();
  }

  @Override
  public ImmutableList<TypeVariableName> getTypeVariables() {
    return mSpecModel.getTypeVariables();
  }

  @Override
  public ImmutableList<StateParamModel> getStateValues() {
    return mSpecModel.getStateValues();
  }

  @Override
  public ImmutableList<InterStageInputParamModel> getInterStageInputs() {
    return mSpecModel.getInterStageInputs();
  }

  @Override
  public ImmutableList<TreePropModel> getTreeProps() {
    return mSpecModel.getTreeProps();
  }

  @Override
  public ImmutableList<EventDeclarationModel> getEventDeclarations() {
    return mSpecModel.getEventDeclarations();
  }

  @Override
  public ImmutableList<BuilderMethodModel> getExtraBuilderMethods() {
    return mSpecModel.getExtraBuilderMethods();
  }

  @Override
  public ImmutableList<RenderDataDiffModel> getRenderDataDiffs() {
    return mSpecModel.getRenderDataDiffs();
  }

  @Override
  public ImmutableList<AnnotationSpec> getClassAnnotations() {
    return mSpecModel.getClassAnnotations();
  }

  @Override
  public ImmutableList<TagModel> getTags() {
    return mSpecModel.getTags();
  }

  @Override
  public String getClassJavadoc() {
    return mSpecModel.getClassJavadoc();
  }

  @Override
  public ImmutableList<PropJavadocModel> getPropJavadocs() {
    return mSpecModel.getPropJavadocs();
  }

  @Override
  public boolean isPublic() {
    return mSpecModel.isPublic();
  }

  @Override
  public ClassName getContextClass() {
    return ClassNames.COMPONENT_CONTEXT;
  }

  @Override
  public ClassName getComponentClass() {
    return mSpecModel.getComponentClass();
  }

  @Override
  public ClassName getStateContainerClass() {
    return ClassNames.STATE_CONTAINER_COMPONENT;
  }

  @Override
  public TypeName getUpdateStateInterface() {
    return ClassNames.COMPONENT_STATE_UPDATE;
  }

  @Override
  public String getScopeMethodName() {
    return "getComponentScope";
  }

  @Override
  public boolean isStylingSupported() {
    return true;
  }

  @Override
  public boolean hasInjectedDependencies() {
    return mSpecModel.hasInjectedDependencies();
  }

  @Override
  public boolean hasDeepCopy() {
    return false;
  }

  @Override
  public boolean shouldGenerateHasState() {
    return true;
  }

  @Override
  public boolean shouldCheckIdInIsEquivalentToMethod() {
    return true;
  }

  @Override
  public DependencyInjectionHelper getDependencyInjectionHelper() {
    return mSpecModel.getDependencyInjectionHelper();
  }

  @Override
  public SpecElementType getSpecElementType() {
    return mSpecModel.getSpecElementType();
  }

  @Override
  public Object getRepresentedObject() {
    return mSpecModel.getRepresentedObject();
  }

  @Override
  public List<SpecModelValidationError> validate(RunMode runMode) {
    return SpecModelValidation.validateMountSpecModel(this, runMode);
  }

  @Override
  public TypeSpec generate() {
    return mMountSpecGenerator.generate(this);
  }

  @Override
  public boolean isPureRender() {
    return mIsPureRender;
  }

  public boolean canMountIncrementally() {
    return mCanMountIncrementally;
  }

  public boolean shouldUseDisplayList() {
    return mShouldUseDisplayList;
  }

  public int getPoolSize() {
    return mPoolSize;
  }

  public boolean canPreallocate() {
    return mCanPreallocate;
  }

  public TypeName getMountType() {
    return mMountType;
  }

  @Override
  public String toString() {
    return "MountSpecModel{"
        + "mSpecModel="
        + mSpecModel
        + ", mIsPureRender="
        + mIsPureRender
        + ", mCanMountIncrementally="
        + mCanMountIncrementally
        + ", mShouldUseDisplayList="
        + mShouldUseDisplayList
        + ", mPoolSize="
        + mPoolSize
        + ", mCanPreallocate="
        + mCanPreallocate
        + ", mMountType="
        + mMountType
        + '}';
  }
}
