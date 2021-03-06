/*
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.litho.sections.processor.integration.resources;

import android.view.View;
import android.widget.TextView;
import com.facebook.litho.ClickEvent;
import com.facebook.litho.Component;
import com.facebook.litho.Diff;
import com.facebook.litho.StateValue;
import com.facebook.litho.annotations.FromEvent;
import com.facebook.litho.annotations.OnCreateInitialState;
import com.facebook.litho.annotations.OnCreateTreeProp;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.OnUpdateState;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.ResType;
import com.facebook.litho.annotations.ShouldUpdate;
import com.facebook.litho.annotations.State;
import com.facebook.litho.annotations.TreeProp;
import com.facebook.litho.sections.Children;
import com.facebook.litho.sections.SectionContext;
import com.facebook.litho.sections.annotations.GroupSectionSpec;
import com.facebook.litho.sections.annotations.OnBindService;
import com.facebook.litho.sections.annotations.OnCreateChildren;
import com.facebook.litho.sections.annotations.OnCreateService;
import com.facebook.litho.sections.annotations.OnDataBound;
import com.facebook.litho.sections.annotations.OnRefresh;
import com.facebook.litho.sections.annotations.OnUnbindService;
import com.facebook.litho.sections.annotations.OnViewportChanged;

/** Comment to be copied in generated section */
@GroupSectionSpec(events = TestEvent.class, isPublic = false)
public class FullGroupSectionSpec<T> implements TestTag {

  static class TreePropWrapper {}

  @OnCreateInitialState
  static <T> void onCreateInitialState(
      SectionContext c, @Prop int prop1, StateValue<T> state1, StateValue<Object> state2) {}

  @OnCreateTreeProp
  static TreePropWrapper onCreateTreeProp(SectionContext c, @TreeProp TreePropWrapper treeProp) {
    return new TreePropWrapper();
  }

  @OnCreateService
  static String onCreateService(SectionContext c, @Prop(optional = true) String prop2) {
    return prop2;
  }

  @OnCreateChildren
  protected static <T> Children onCreateChildren(
      SectionContext c,
      @Prop Component prop3,
      @Prop(resType = ResType.STRING) String prop4,
      @State T state1) {
    return null;
  }

  @OnUpdateState
  protected static void updateState(StateValue<Object> state2, @Param Object param) {}

  @OnBindService
  static void bindService(
      SectionContext c,
      String service,
      @Prop int prop1,
      @State(canUpdateLazily = true) Object state2) {}

  @OnUnbindService
  static void unbindService(
      SectionContext c,
      String service,
      @Prop int prop1,
      @State(canUpdateLazily = true) Object state2) {}

  @OnRefresh
  static void onRefresh(SectionContext c, String service, @Prop(optional = true) String prop2) {}

  @OnDataBound
  static void onDataBound(
      SectionContext c, @Prop Component prop3, @State(canUpdateLazily = true) Object state2) {}

  @ShouldUpdate
  static boolean shouldUpdate(@Prop Diff<Integer> prop1) {
    return true;
  }

  @OnViewportChanged
  static <T> void onViewportChanged(
      SectionContext c,
      int firstVisibleIndex,
      int lastVisibleIndex,
      int totalCount,
      int firstFullyVisibleIndex,
      int lastFullyVisibleIndex,
      @State T state1,
      @State(canUpdateLazily = true) Object state2,
      @Prop int prop1,
      @Prop(optional = true) String prop2,
      @Prop Component prop3) {}

  @OnEvent(ClickEvent.class)
  static void testEvent(
      SectionContext c,
      @FromEvent(baseClass = View.class) TextView view,
      @Param int someParam,
      @State(canUpdateLazily = true) Object state2,
      @Prop(optional = true) String prop2) {}
}
