/*
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.litho;

import android.animation.StateListAnimator;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.StyleRes;
import android.util.SparseArray;
import android.view.ViewOutlineProvider;
import com.facebook.infer.annotation.ThreadConfined;
import com.facebook.litho.config.ComponentsConfiguration;
import com.facebook.litho.reference.Reference;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaDirection;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaPositionType;
import java.util.ArrayList;
import java.util.List;

/** Internal class that holds props that are common to all {@link Component}s. */
@ThreadConfined(ThreadConfined.ANY)
class CommonPropsHolder implements CommonProps, CommonPropsCopyable {

  // Flags used to indicate that a certain attribute was explicitly set on the node.
  private static final byte PFLAG_POSITION_TYPE_IS_SET = 1 << 1;
  private static final byte PFLAG_POSITION_IS_SET = 1 << 2;
  private static final byte PFLAG_WIDTH_IS_SET = 1 << 3;
  private static final byte PFLAG_HEIGHT_IS_SET = 1 << 4;
  private static final byte PFLAG_BACKGROUND_IS_SET = 1 << 5;
  private static final byte PFLAG_TEST_KEY_IS_SET = 1 << 6;

  @Nullable private OtherProps mOtherProps;

  private byte mPrivateFlags;
  @Nullable private NodeInfo mNodeInfo;

  @Nullable private YogaPositionType mPositionType;
  @Nullable private YogaEdgesWithInts mPositions;
  private int mWidthPx;
  private int mHeightPx;
  @Nullable private Reference<? extends Drawable> mBackground;
  @Nullable private String mTestKey;
  private boolean mWrapInView;
  @AttrRes private int mDefStyleAttr;
  @StyleRes private int mDefStyleRes;

  private OtherProps getOrCreateOtherProps() {
    if (mOtherProps == null) {
      mOtherProps = new OtherProps();
    }

    return mOtherProps;
  }

  void setStyle(@AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
    mDefStyleAttr = defStyleAttr;
    mDefStyleRes = defStyleRes;
  }

  @Override
  @AttrRes
  public int getDefStyleAttr() {
    return mDefStyleAttr;
  }

  @Override
  @StyleRes
  public int getDefStyleRes() {
    return mDefStyleRes;
  }

  void positionType(YogaPositionType positionType) {
    mPrivateFlags |= PFLAG_POSITION_TYPE_IS_SET;
    mPositionType = positionType;
  }

  @Override
  @Nullable
  public YogaPositionType getPositionType() {
    return mPositionType;
  }

  void positionPx(YogaEdge edge, @Px int position) {
    mPrivateFlags |= PFLAG_POSITION_IS_SET;
    if (mPositions == null) {
      mPositions =
          ComponentsConfiguration.useNewYogaEdge
              ? new YogaEdgesWithIntsImplOptimized()
              : new YogaEdgesWithIntsImpl();
    }

    mPositions.add(edge, position);
  }

  void widthPx(@Px int width) {
    mPrivateFlags |= PFLAG_WIDTH_IS_SET;
    mWidthPx = width;
  }

  @Override
  public int getWidthPx() {
    return mWidthPx;
  }

  void heightPx(@Px int height) {
    mPrivateFlags |= PFLAG_HEIGHT_IS_SET;
    mHeightPx = height;
  }

  @Override
  public int getHeightPx() {
    return mHeightPx;
  }

  void background(Reference<? extends Drawable> background) {
    mPrivateFlags |= PFLAG_BACKGROUND_IS_SET;
    mBackground = background;
  }

  @Override
  @Nullable
  public Reference<? extends Drawable> getBackground() {
    return mBackground;
  }

  void testKey(String testKey) {
    mPrivateFlags |= PFLAG_TEST_KEY_IS_SET;
    mTestKey = testKey;
  }

  @Override
  @Nullable
  public String getTestKey() {
    return mTestKey;
  }

  void wrapInView() {
    mWrapInView = true;
  }

  @Override
  public boolean isWrapInView() {
    return mWrapInView;
  }

  void layoutDirection(YogaDirection direction) {
    getOrCreateOtherProps().layoutDirection(direction);
  }

  @Override
  @Nullable
  public YogaDirection getLayoutDirection() {
    return getOrCreateOtherProps().mLayoutDirection;
  }

  void alignSelf(YogaAlign alignSelf) {
    getOrCreateOtherProps().alignSelf(alignSelf);
  }

  @Override
  @Nullable
  public YogaAlign getAlignSelf() {
    return getOrCreateOtherProps().mAlignSelf;
  }

  void flex(float flex) {
    getOrCreateOtherProps().flex(flex);
  }

  @Override
  public float getFlex() {
    return getOrCreateOtherProps().mFlex;
  }

  void flexGrow(float flexGrow) {
    getOrCreateOtherProps().flexGrow(flexGrow);
  }

  @Override
  public float getFlexGrow() {
    return getOrCreateOtherProps().mFlexGrow;
  }

  void flexShrink(float flexShrink) {
    getOrCreateOtherProps().flexShrink(flexShrink);
  }

  @Override
  public float getFlexShrink() {
    return getOrCreateOtherProps().mFlexShrink;
  }

  void flexBasisPx(@Px int flexBasis) {
    getOrCreateOtherProps().flexBasisPx(flexBasis);
  }

  @Override
  @Px
  public int getFlexBasisPx() {
    return getOrCreateOtherProps().mFlexBasisPx;
  }

  void flexBasisPercent(float percent) {
    getOrCreateOtherProps().flexBasisPercent(percent);
  }

  @Override
  public float getFlexBasisPercent() {
    return getOrCreateOtherProps().mFlexBasisPercent;
  }

  void importantForAccessibility(int importantForAccessibility) {
    getOrCreateOtherProps().importantForAccessibility(importantForAccessibility);
  }

  @Override
  public int getImportantForAccessibility() {
    return getOrCreateOtherProps().mImportantForAccessibility;
  }

  void duplicateParentState(boolean duplicateParentState) {
    getOrCreateOtherProps().duplicateParentState(duplicateParentState);
  }

  @Override
  public boolean getDuplicateParentState() {
    return getOrCreateOtherProps().mDuplicateParentState;
  }

  void marginPx(YogaEdge edge, @Px int margin) {
    getOrCreateOtherProps().marginPx(edge, margin);
  }

  void marginPercent(YogaEdge edge, float percent) {
    getOrCreateOtherProps().marginPercent(edge, percent);
  }

  void marginAuto(YogaEdge edge) {
    getOrCreateOtherProps().marginAuto(edge);
  }

  void paddingPx(YogaEdge edge, @Px int padding) {
    getOrCreateOtherProps().paddingPx(edge, padding);
  }

  void paddingPercent(YogaEdge edge, float percent) {
    getOrCreateOtherProps().paddingPercent(edge, percent);
  }

  void border(Border border) {
    getOrCreateOtherProps().border(border);
  }

  @Override
  @Nullable
  public Border getBorder() {
    return getOrCreateOtherProps().mBorder;
  }

  void stateListAnimator(StateListAnimator stateListAnimator) {
    getOrCreateOtherProps().stateListAnimator(stateListAnimator);
  }

  @Nullable
  @Override
  public StateListAnimator getStateListAnimator() {
    return getOrCreateOtherProps().mStateListAnimator;
  }

  void positionPercent(YogaEdge edge, float percent) {
    getOrCreateOtherProps().positionPercent(edge, percent);
  }

  void widthPercent(float percent) {
    getOrCreateOtherProps().widthPercent(percent);
  }

  void minWidthPx(@Px int minWidth) {
    getOrCreateOtherProps().minWidthPx(minWidth);
  }

  void minWidthPercent(float percent) {
    getOrCreateOtherProps().minWidthPercent(percent);
  }

  void maxWidthPx(@Px int maxWidth) {
    getOrCreateOtherProps().maxWidthPx(maxWidth);
  }

  void maxWidthPercent(float percent) {
    getOrCreateOtherProps().maxWidthPercent(percent);
  }

  void heightPercent(float percent) {
    getOrCreateOtherProps().heightPercent(percent);
  }

  void minHeightPx(@Px int minHeight) {
    getOrCreateOtherProps().minHeightPx(minHeight);
  }

  void minHeightPercent(float percent) {
    getOrCreateOtherProps().minHeightPercent(percent);
  }

  void maxHeightPx(@Px int maxHeight) {
    getOrCreateOtherProps().maxHeightPx(maxHeight);
  }

  void maxHeightPercent(float percent) {
    getOrCreateOtherProps().maxHeightPercent(percent);
  }

  void aspectRatio(float aspectRatio) {
    getOrCreateOtherProps().aspectRatio(aspectRatio);
  }

  @Override
  public float getAspectRatio() {
    return getOrCreateOtherProps().mAspectRatio;
  }

  void touchExpansionPx(YogaEdge edge, @Px int touchExpansion) {
    getOrCreateOtherProps().touchExpansionPx(edge, touchExpansion);
  }

  void foreground(Drawable foreground) {
    getOrCreateOtherProps().foreground(foreground);
  }

  @Override
  @Nullable
  public Drawable getForeground() {
    return getOrCreateOtherProps().mForeground;
  }

  void clickHandler(EventHandler<ClickEvent> clickHandler) {
    getOrCreateNodeInfo().setClickHandler(clickHandler);
  }

  @Override
  @Nullable
  public EventHandler<ClickEvent> getClickHandler() {
    return getOrCreateNodeInfo().getClickHandler();
  }

  void longClickHandler(EventHandler<LongClickEvent> longClickHandler) {
    getOrCreateNodeInfo().setLongClickHandler(longClickHandler);
  }

  @Override
  @Nullable
  public EventHandler<LongClickEvent> getLongClickHandler() {
    return getOrCreateNodeInfo().getLongClickHandler();
  }

  void focusChangeHandler(EventHandler<FocusChangedEvent> focusChangeHandler) {
    getOrCreateNodeInfo().setFocusChangeHandler(focusChangeHandler);
  }

  @Override
  @Nullable
  public EventHandler<FocusChangedEvent> getFocusChangeHandler() {
    return getOrCreateNodeInfo().getFocusChangeHandler();
  }

  void touchHandler(EventHandler<TouchEvent> touchHandler) {
    getOrCreateNodeInfo().setTouchHandler(touchHandler);
  }

  @Override
  @Nullable
  public EventHandler<TouchEvent> getTouchHandler() {
    return getOrCreateNodeInfo().getTouchHandler();
  }

  void interceptTouchHandler(EventHandler<InterceptTouchEvent> interceptTouchHandler) {
    getOrCreateNodeInfo().setInterceptTouchHandler(interceptTouchHandler);
  }

  @Override
  @Nullable
  public EventHandler<InterceptTouchEvent> getInterceptTouchHandler() {
    return getOrCreateNodeInfo().getInterceptTouchHandler();
  }

  void focusable(boolean isFocusable) {
    getOrCreateNodeInfo().setFocusable(isFocusable);
  }

  @Override
  public boolean getFocusable() {
    return getOrCreateNodeInfo().getFocusState() == NodeInfo.FOCUS_SET_TRUE;
  }

  void enabled(boolean isEnabled) {
    getOrCreateNodeInfo().setEnabled(isEnabled);
  }

  @Override
  public boolean getEnabled() {
    return getOrCreateNodeInfo().getEnabledState() == NodeInfo.ENABLED_SET_TRUE;
  }

  void selected(boolean isSelected) {
    getOrCreateNodeInfo().setSelected(isSelected);
  }

  @Override
  public boolean getSelected() {
    return getOrCreateNodeInfo().getSelectedState() == NodeInfo.ENABLED_SET_TRUE;
  }

  void visibleHeightRatio(float visibleHeightRatio) {
    getOrCreateOtherProps().visibleHeightRatio(visibleHeightRatio);
  }

  @Override
  public float getVisibleHeightRatio() {
    return getOrCreateOtherProps().mVisibleHeightRatio;
  }

  void visibleWidthRatio(float visibleWidthRatio) {
    getOrCreateOtherProps().visibleWidthRatio(visibleWidthRatio);
  }

  @Override
  public float getVisibleWidthRatio() {
    return getOrCreateOtherProps().mVisibleWidthRatio;
  }

  void visibleHandler(EventHandler<VisibleEvent> visibleHandler) {
    getOrCreateOtherProps().visibleHandler(visibleHandler);
  }

  @Override
  @Nullable
  public EventHandler<VisibleEvent> getVisibleHandler() {
    return getOrCreateOtherProps().mVisibleHandler;
  }

  void focusedHandler(EventHandler<FocusedVisibleEvent> focusedHandler) {
    getOrCreateOtherProps().focusedHandler(focusedHandler);
  }

  @Override
  @Nullable
  public EventHandler<FocusedVisibleEvent> getFocusedHandler() {
    return getOrCreateOtherProps().mFocusedHandler;
  }

  void unfocusedHandler(EventHandler<UnfocusedVisibleEvent> unfocusedHandler) {
    getOrCreateOtherProps().unfocusedHandler(unfocusedHandler);
  }

  @Override
  @Nullable
  public EventHandler<UnfocusedVisibleEvent> getUnfocusedHandler() {
    return getOrCreateOtherProps().mUnfocusedHandler;
  }

  void fullImpressionHandler(EventHandler<FullImpressionVisibleEvent> fullImpressionHandler) {
    getOrCreateOtherProps().fullImpressionHandler(fullImpressionHandler);
  }

  @Override
  @Nullable
  public EventHandler<FullImpressionVisibleEvent> getFullImpressionHandler() {
    return getOrCreateOtherProps().mFullImpressionHandler;
  }

  void invisibleHandler(EventHandler<InvisibleEvent> invisibleHandler) {
    getOrCreateOtherProps().invisibleHandler(invisibleHandler);
  }

  @Override
  @Nullable
  public EventHandler<InvisibleEvent> getInvisibleHandler() {
    return getOrCreateOtherProps().mInvisibleHandler;
  }

  void contentDescription(CharSequence contentDescription) {
    getOrCreateNodeInfo().setContentDescription(contentDescription);
  }

  @Override
  @Nullable
  public CharSequence getContentDescription() {
    return getOrCreateNodeInfo().getContentDescription();
  }

  void viewTag(Object viewTag) {
    getOrCreateNodeInfo().setViewTag(viewTag);
  }

  @Override
  @Nullable
  public Object getViewTag() {
    return getOrCreateNodeInfo().getViewTag();
  }

  void viewTags(SparseArray<Object> viewTags) {
    getOrCreateNodeInfo().setViewTags(viewTags);
  }

  @Override
  @Nullable
  public SparseArray getViewTags() {
    final SparseArray<Object> viewTags = getOrCreateNodeInfo().getViewTags();
    return viewTags == null ? null : viewTags.clone();
  }

  void shadowElevationPx(float shadowElevation) {
    getOrCreateNodeInfo().setShadowElevation(shadowElevation);
  }

  @Override
  public float getShadowElevationPx() {
    return getOrCreateNodeInfo().getShadowElevation();
  }

  void outlineProvider(ViewOutlineProvider outlineProvider) {
    getOrCreateNodeInfo().setOutlineProvider(outlineProvider);
  }

  @Override
  @Nullable
  public ViewOutlineProvider getOutlineProvider() {
    return getOrCreateNodeInfo().getOutlineProvider();
  }

  void clipToOutline(boolean clipToOutline) {
    getOrCreateNodeInfo().setClipToOutline(clipToOutline);
  }

  @Override
  public boolean getClipToOutline() {
    return getOrCreateNodeInfo().getClipToOutline();
  }

  void accessibilityRole(@AccessibilityRole.AccessibilityRoleType String role) {
    getOrCreateNodeInfo().setAccessibilityRole(role);
  }

  @Override
  @AccessibilityRole.AccessibilityRoleType
  @Nullable
  public String getAccessibilityRole() {
    return getOrCreateNodeInfo().getAccessibilityRole();
  }

  void dispatchPopulateAccessibilityEventHandler(
      EventHandler<DispatchPopulateAccessibilityEventEvent>
          dispatchPopulateAccessibilityEventHandler) {
    getOrCreateNodeInfo()
        .setDispatchPopulateAccessibilityEventHandler(dispatchPopulateAccessibilityEventHandler);
  }

  @Override
  @Nullable
  public EventHandler<DispatchPopulateAccessibilityEventEvent>
      getDispatchPopulateAccessibilityEventHandler() {
    return getOrCreateNodeInfo().getDispatchPopulateAccessibilityEventHandler();
  }

  void onInitializeAccessibilityEventHandler(
      EventHandler<OnInitializeAccessibilityEventEvent> onInitializeAccessibilityEventHandler) {
    getOrCreateNodeInfo()
        .setOnInitializeAccessibilityEventHandler(onInitializeAccessibilityEventHandler);
  }

  @Override
  @Nullable
  public EventHandler<OnInitializeAccessibilityEventEvent>
      getOnInitializeAccessibilityEventHandler() {
    return getOrCreateNodeInfo().getOnInitializeAccessibilityEventHandler();
  }

  void onInitializeAccessibilityNodeInfoHandler(
      EventHandler<OnInitializeAccessibilityNodeInfoEvent>
          onInitializeAccessibilityNodeInfoHandler) {
    getOrCreateNodeInfo()
        .setOnInitializeAccessibilityNodeInfoHandler(onInitializeAccessibilityNodeInfoHandler);
  }

  @Override
  @Nullable
  public EventHandler<OnInitializeAccessibilityNodeInfoEvent>
      getOnInitializeAccessibilityNodeInfoHandler() {
    return getOrCreateNodeInfo().getOnInitializeAccessibilityNodeInfoHandler();
  }

  void onPopulateAccessibilityEventHandler(
      EventHandler<OnPopulateAccessibilityEventEvent> onPopulateAccessibilityEventHandler) {
    getOrCreateNodeInfo()
        .setOnPopulateAccessibilityEventHandler(onPopulateAccessibilityEventHandler);
  }

  @Override
  @Nullable
  public EventHandler<OnPopulateAccessibilityEventEvent> getOnPopulateAccessibilityEventHandler() {
    return getOrCreateNodeInfo().getOnPopulateAccessibilityEventHandler();
  }

  void onRequestSendAccessibilityEventHandler(
      EventHandler<OnRequestSendAccessibilityEventEvent> onRequestSendAccessibilityEventHandler) {
    getOrCreateNodeInfo()
        .setOnRequestSendAccessibilityEventHandler(onRequestSendAccessibilityEventHandler);
  }

  @Nullable
  @Override
  public EventHandler<OnRequestSendAccessibilityEventEvent>
      getOnRequestSendAccessibilityEventHandler() {
    return getOrCreateNodeInfo().getOnRequestSendAccessibilityEventHandler();
  }

  void performAccessibilityActionHandler(
      EventHandler<PerformAccessibilityActionEvent> performAccessibilityActionHandler) {
    getOrCreateNodeInfo().setPerformAccessibilityActionHandler(performAccessibilityActionHandler);
  }

  @Nullable
  @Override
  public EventHandler<PerformAccessibilityActionEvent> getPerformAccessibilityActionHandler() {
    return getOrCreateNodeInfo().getPerformAccessibilityActionHandler();
  }

  void sendAccessibilityEventHandler(
      EventHandler<SendAccessibilityEventEvent> sendAccessibilityEventHandler) {
    getOrCreateNodeInfo().setSendAccessibilityEventHandler(sendAccessibilityEventHandler);
  }

  @Nullable
  @Override
  public EventHandler<SendAccessibilityEventEvent> getSendAccessibilityEventHandler() {
    return getOrCreateNodeInfo().getSendAccessibilityEventHandler();
  }

  void sendAccessibilityEventUncheckedHandler(
      EventHandler<SendAccessibilityEventUncheckedEvent> sendAccessibilityEventUncheckedHandler) {
    getOrCreateNodeInfo()
        .setSendAccessibilityEventUncheckedHandler(sendAccessibilityEventUncheckedHandler);
  }

  @Nullable
  @Override
  public EventHandler<SendAccessibilityEventUncheckedEvent>
      getSendAccessibilityEventUncheckedHandler() {
    return getOrCreateNodeInfo().getSendAccessibilityEventUncheckedHandler();
  }

  void scale(float scale) {
    getOrCreateNodeInfo().setScale(scale);
  }

  @Override
  public float getScale() {
    return getOrCreateNodeInfo().getScale();
  }

  void alpha(float alpha) {
    getOrCreateNodeInfo().setAlpha(alpha);
  }

  @Override
  public float getAlpha() {
    return getOrCreateNodeInfo().getAlpha();
  }

  void rotation(float rotation) {
    getOrCreateNodeInfo().setRotation(rotation);
  }

  @Override
  public float getRotation() {
    return getOrCreateNodeInfo().getRotation();
  }

  void transitionKey(String key) {
    getOrCreateOtherProps().transitionKey(key);
  }

  @Override
  @Nullable
  public String getTransitionKey() {
    return getOrCreateOtherProps().mTransitionKey;
  }

  private NodeInfo getOrCreateNodeInfo() {
    if (mNodeInfo == null) {
      mNodeInfo = NodeInfo.acquire();
    }

    return mNodeInfo;
  }

  @Override
  public void copyInto(ComponentContext c, InternalNode node) {
    c.applyStyle(node, mDefStyleAttr, mDefStyleRes);

    if (mNodeInfo != null) {
      mNodeInfo.copyInto(node);
    }

    if ((mPrivateFlags & PFLAG_BACKGROUND_IS_SET) != 0L) {
      node.background(mBackground);
    }
    if ((mPrivateFlags & PFLAG_TEST_KEY_IS_SET) != 0L) {
      node.testKey(mTestKey);
    }
    if ((mPrivateFlags & PFLAG_POSITION_TYPE_IS_SET) != 0L) {
      node.positionType(mPositionType);
    }
    if ((mPrivateFlags & PFLAG_POSITION_IS_SET) != 0L) {
      for (int i = 0; i < mPositions.size(); i++) {
        node.positionPx(mPositions.getEdge(i), mPositions.getValue(i));
      }
    }
    if ((mPrivateFlags & PFLAG_WIDTH_IS_SET) != 0L) {
      node.widthPx(mWidthPx);
    }
    if ((mPrivateFlags & PFLAG_HEIGHT_IS_SET) != 0L) {
      node.heightPx(mHeightPx);
    }
    
    if (mWrapInView) {
      node.wrapInView();
    }

    if (mOtherProps != null) {
      mOtherProps.copyInto(node);
    }
  }

  private static class OtherProps {
    // Flags used to indicate that a certain attribute was explicitly set on the node.
    private static final long PFLAG_LAYOUT_DIRECTION_IS_SET = 1L << 0;
    private static final long PFLAG_ALIGN_SELF_IS_SET = 1L << 1;
    private static final long PFLAG_FLEX_IS_SET = 1L << 2;
    private static final long PFLAG_FLEX_GROW_IS_SET = 1L << 3;
    private static final long PFLAG_FLEX_SHRINK_IS_SET = 1L << 4;
    private static final long PFLAG_FLEX_BASIS_IS_SET = 1L << 5;
    private static final long PFLAG_IMPORTANT_FOR_ACCESSIBILITY_IS_SET = 1L << 6;
    private static final long PFLAG_DUPLICATE_PARENT_STATE_IS_SET = 1L << 7;
    private static final long PFLAG_MARGIN_IS_SET = 1L << 8;
    private static final long PFLAG_PADDING_IS_SET = 1L << 9;
    private static final long PFLAG_POSITION_PERCENT_IS_SET = 1L << 10;
    private static final long PFLAG_MIN_WIDTH_IS_SET = 1L << 11;
    private static final long PFLAG_MAX_WIDTH_IS_SET = 1L << 12;
    private static final long PFLAG_MIN_HEIGHT_IS_SET = 1L << 13;
    private static final long PFLAG_MAX_HEIGHT_IS_SET = 1L << 14;
    private static final long PFLAG_FOREGROUND_IS_SET = 1L << 15;
    private static final long PFLAG_VISIBLE_HANDLER_IS_SET = 1L << 16;
    private static final long PFLAG_FOCUSED_HANDLER_IS_SET = 1L << 17;
    private static final long PFLAG_FULL_IMPRESSION_HANDLER_IS_SET = 1L << 18;
    private static final long PFLAG_INVISIBLE_HANDLER_IS_SET = 1L << 19;
    private static final long PFLAG_UNFOCUSED_HANDLER_IS_SET = 1L << 20;
    private static final long PFLAG_TOUCH_EXPANSION_IS_SET = 1L << 21;
    private static final long PFLAG_ASPECT_RATIO_IS_SET = 1L << 22;
    private static final long PFLAG_TRANSITION_KEY_IS_SET = 1L << 23;
    private static final long PFLAG_WRAP_IN_VIEW_IS_SET = 1L << 24;
    private static final long PFLAG_VISIBLE_HEIGHT_RATIO_IS_SET = 1L << 25;
    private static final long PFLAG_VISIBLE_WIDTH_RATIO_IS_SET = 1L << 26;
    private static final long PFLAG_FLEX_BASIS_PERCENT_IS_SET = 1L << 27;
    private static final long PFLAG_MARGIN_PERCENT_IS_SET = 1L << 28;
    private static final long PFLAG_MARGIN_AUTO_IS_SET = 1L << 29;
    private static final long PFLAG_PADDING_PERCENT_IS_SET = 1L << 30;
    private static final long PFLAG_WIDTH_PERCENT_IS_SET = 1L << 31;
    private static final long PFLAG_MIN_WIDTH_PERCENT_IS_SET = 1L << 32;
    private static final long PFLAG_MAX_WIDTH_PERCENT_IS_SET = 1L << 33;
    private static final long PFLAG_HEIGHT_PERCENT_IS_SET = 1L << 34;
    private static final long PFLAG_MIN_HEIGHT_PERCENT_IS_SET = 1L << 35;
    private static final long PFLAG_MAX_HEIGHT_PERCENT_IS_SET = 1L << 36;
    private static final long PFLAG_BORDER_IS_SET = 1L << 37;
    private static final long PFLAG_STATE_LIST_ANIMATOR_IS_SET = 1L << 38;

    private long mPrivateFlags;

    private float mVisibleHeightRatio;
    private float mVisibleWidthRatio;
    @Nullable private EventHandler<VisibleEvent> mVisibleHandler;
    @Nullable private EventHandler<FocusedVisibleEvent> mFocusedHandler;
    @Nullable private EventHandler<UnfocusedVisibleEvent> mUnfocusedHandler;
    @Nullable private EventHandler<FullImpressionVisibleEvent> mFullImpressionHandler;
    @Nullable private EventHandler<InvisibleEvent> mInvisibleHandler;
    @Nullable private YogaDirection mLayoutDirection;
    @Nullable private YogaAlign mAlignSelf;
    private float mFlex;
    private float mFlexGrow;
    private float mFlexShrink;
    @Px private int mFlexBasisPx;
    private float mFlexBasisPercent;
    private int mImportantForAccessibility;
    private boolean mDuplicateParentState;
    @Nullable private YogaEdgesWithInts mMargins;
    @Nullable private YogaEdgesWithFloats mMarginPercents;
    @Nullable private List<YogaEdge> mMarginAutos;
    @Nullable private YogaEdgesWithInts mPaddings;
    @Nullable private YogaEdgesWithFloats mPaddingPercents;
    @Nullable private YogaEdgesWithFloats mPositionPercents;
    @Nullable private YogaEdgesWithInts mTouchExpansions;
    private float mWidthPercent;
    @Px private int mMinWidthPx;
    private float mMinWidthPercent;
    @Px private int mMaxWidthPx;
    private float mMaxWidthPercent;
    private float mHeightPercent;
    @Px private int mMinHeightPx;
    private float mMinHeightPercent;
    @Px private int mMaxHeightPx;
    private float mMaxHeightPercent;
    private float mAspectRatio;
    @Nullable private Drawable mForeground;
    @Nullable private String mTransitionKey;
    @Nullable private Border mBorder;
    @Nullable private StateListAnimator mStateListAnimator;

    private void layoutDirection(YogaDirection direction) {
      mPrivateFlags |= PFLAG_LAYOUT_DIRECTION_IS_SET;
      mLayoutDirection = direction;
    }

    private void alignSelf(YogaAlign alignSelf) {
      mPrivateFlags |= PFLAG_ALIGN_SELF_IS_SET;
      mAlignSelf = alignSelf;
    }

    private void flex(float flex) {
      mPrivateFlags |= PFLAG_FLEX_IS_SET;
      mFlex = flex;
    }

    private void flexGrow(float flexGrow) {
      mPrivateFlags |= PFLAG_FLEX_GROW_IS_SET;
      mFlexGrow = flexGrow;
    }

    private void flexShrink(float flexShrink) {
      mPrivateFlags |= PFLAG_FLEX_SHRINK_IS_SET;
      mFlexShrink = flexShrink;
    }

    private void flexBasisPx(@Px int flexBasis) {
      mPrivateFlags |= PFLAG_FLEX_BASIS_IS_SET;
      mFlexBasisPx = flexBasis;
    }

    private void flexBasisPercent(float percent) {
      mPrivateFlags |= PFLAG_FLEX_BASIS_PERCENT_IS_SET;
      mFlexBasisPercent = percent;
    }

    private void importantForAccessibility(int importantForAccessibility) {
      mPrivateFlags |= PFLAG_IMPORTANT_FOR_ACCESSIBILITY_IS_SET;
      mImportantForAccessibility = importantForAccessibility;
    }

    private void duplicateParentState(boolean duplicateParentState) {
      mPrivateFlags |= PFLAG_DUPLICATE_PARENT_STATE_IS_SET;
      mDuplicateParentState = duplicateParentState;
    }

    private void marginPx(YogaEdge edge, @Px int margin) {
      mPrivateFlags |= PFLAG_MARGIN_IS_SET;

      if (mMargins == null) {
        mMargins =
            ComponentsConfiguration.useNewYogaEdge
                ? new YogaEdgesWithIntsImplOptimized()
                : new YogaEdgesWithIntsImpl();
      }
      mMargins.add(edge, margin);
    }

    private void marginPercent(YogaEdge edge, float percent) {
      mPrivateFlags |= PFLAG_MARGIN_PERCENT_IS_SET;
      if (mMarginPercents == null) {
        mMarginPercents = new YogaEdgesWithFloats();
      }
      mMarginPercents.add(edge, percent);
    }

    private void marginAuto(YogaEdge edge) {
      mPrivateFlags |= PFLAG_MARGIN_AUTO_IS_SET;
      if (mMarginAutos == null) {
        mMarginAutos = new ArrayList<>(2);
      }
      mMarginAutos.add(edge);
    }

    private void paddingPx(YogaEdge edge, @Px int padding) {
      mPrivateFlags |= PFLAG_PADDING_IS_SET;
      if (mPaddings == null) {
        mPaddings =
            ComponentsConfiguration.useNewYogaEdge
                ? new YogaEdgesWithIntsImplOptimized()
                : new YogaEdgesWithIntsImpl();
      }
      mPaddings.add(edge, padding);
    }

    private void paddingPercent(YogaEdge edge, float percent) {
      mPrivateFlags |= PFLAG_PADDING_PERCENT_IS_SET;
      if (mPaddingPercents == null) {
        mPaddingPercents = new YogaEdgesWithFloats();
      }
      mPaddingPercents.add(edge, percent);
    }

    private void border(Border border) {
      mPrivateFlags |= PFLAG_BORDER_IS_SET;
      mBorder = border;
    }

    private void positionPercent(YogaEdge edge, float percent) {
      mPrivateFlags |= PFLAG_POSITION_PERCENT_IS_SET;
      if (mPositionPercents == null) {
        mPositionPercents = new YogaEdgesWithFloats();
      }
      mPositionPercents.add(edge, percent);
    }

    private void widthPercent(float percent) {
      mPrivateFlags |= PFLAG_WIDTH_PERCENT_IS_SET;
      mWidthPercent = percent;
    }

    private void minWidthPx(@Px int minWidth) {
      mPrivateFlags |= PFLAG_MIN_WIDTH_IS_SET;
      mMinWidthPx = minWidth;
    }

    private void minWidthPercent(float percent) {
      mPrivateFlags |= PFLAG_MIN_WIDTH_PERCENT_IS_SET;
      mMinWidthPercent = percent;
    }

    private void maxWidthPx(@Px int maxWidth) {
      mPrivateFlags |= PFLAG_MAX_WIDTH_IS_SET;
      mMaxWidthPx = maxWidth;
    }

    private void maxWidthPercent(float percent) {
      mPrivateFlags |= PFLAG_MAX_WIDTH_PERCENT_IS_SET;
      mMaxWidthPercent = percent;
    }

    private void heightPercent(float percent) {
      mPrivateFlags |= PFLAG_HEIGHT_PERCENT_IS_SET;
      mHeightPercent = percent;
    }

    private void minHeightPx(@Px int minHeight) {
      mPrivateFlags |= PFLAG_MIN_HEIGHT_IS_SET;
      mMinHeightPx = minHeight;
    }

    private void minHeightPercent(float percent) {
      mPrivateFlags |= PFLAG_MIN_HEIGHT_PERCENT_IS_SET;
      mMinHeightPercent = percent;
    }

    private void maxHeightPx(@Px int maxHeight) {
      mPrivateFlags |= PFLAG_MAX_HEIGHT_IS_SET;
      mMaxHeightPx = maxHeight;
    }

    private void maxHeightPercent(float percent) {
      mPrivateFlags |= PFLAG_MAX_HEIGHT_PERCENT_IS_SET;
      mMaxHeightPercent = percent;
    }

    private void aspectRatio(float aspectRatio) {
      mPrivateFlags |= PFLAG_ASPECT_RATIO_IS_SET;
      mAspectRatio = aspectRatio;
    }

    private void touchExpansionPx(YogaEdge edge, @Px int touchExpansion) {
      mPrivateFlags |= PFLAG_TOUCH_EXPANSION_IS_SET;
      if (mTouchExpansions == null) {
        mTouchExpansions =
            ComponentsConfiguration.useNewYogaEdge
                ? new YogaEdgesWithIntsImplOptimized()
                : new YogaEdgesWithIntsImpl();
      }
      mTouchExpansions.add(edge, touchExpansion);
    }

    private void foreground(Drawable foreground) {
      mPrivateFlags |= PFLAG_FOREGROUND_IS_SET;
      mForeground = foreground;
    }

    private void visibleHeightRatio(float visibleHeightRatio) {
      mPrivateFlags |= PFLAG_VISIBLE_HEIGHT_RATIO_IS_SET;
      mVisibleHeightRatio = visibleHeightRatio;
    }

    private void visibleWidthRatio(float visibleWidthRatio) {
      mPrivateFlags |= PFLAG_VISIBLE_WIDTH_RATIO_IS_SET;
      mVisibleWidthRatio = visibleWidthRatio;
    }

    private void visibleHandler(EventHandler<VisibleEvent> visibleHandler) {
      mPrivateFlags |= PFLAG_VISIBLE_HANDLER_IS_SET;
      mVisibleHandler = visibleHandler;
    }

    private void focusedHandler(EventHandler<FocusedVisibleEvent> focusedHandler) {
      mPrivateFlags |= PFLAG_FOCUSED_HANDLER_IS_SET;
      mFocusedHandler = focusedHandler;
    }

    private void unfocusedHandler(EventHandler<UnfocusedVisibleEvent> unfocusedHandler) {
      mPrivateFlags |= PFLAG_UNFOCUSED_HANDLER_IS_SET;
      mUnfocusedHandler = unfocusedHandler;
    }

    private void fullImpressionHandler(
        EventHandler<FullImpressionVisibleEvent> fullImpressionHandler) {
      mPrivateFlags |= PFLAG_FULL_IMPRESSION_HANDLER_IS_SET;
      mFullImpressionHandler = fullImpressionHandler;
    }

    private void invisibleHandler(EventHandler<InvisibleEvent> invisibleHandler) {
      mPrivateFlags |= PFLAG_INVISIBLE_HANDLER_IS_SET;
      mInvisibleHandler = invisibleHandler;
    }

    private void transitionKey(String key) {
      mPrivateFlags |= PFLAG_TRANSITION_KEY_IS_SET;
      mTransitionKey = key;
    }

    private void stateListAnimator(StateListAnimator stateListAnimator) {
      mPrivateFlags |= PFLAG_STATE_LIST_ANIMATOR_IS_SET;
      mStateListAnimator = stateListAnimator;
    }

    void copyInto(InternalNode node) {
      if ((mPrivateFlags & PFLAG_LAYOUT_DIRECTION_IS_SET) != 0L) {
        node.layoutDirection(mLayoutDirection);
      }
      if ((mPrivateFlags & PFLAG_IMPORTANT_FOR_ACCESSIBILITY_IS_SET) != 0L) {
        node.importantForAccessibility(mImportantForAccessibility);
      }
      if ((mPrivateFlags & PFLAG_DUPLICATE_PARENT_STATE_IS_SET) != 0L) {
        node.duplicateParentState(mDuplicateParentState);
      }
      if ((mPrivateFlags & PFLAG_FOREGROUND_IS_SET) != 0L) {
        node.foreground(mForeground);
      }
      if ((mPrivateFlags & PFLAG_WRAP_IN_VIEW_IS_SET) != 0L) {
        node.wrapInView();
      }
      if ((mPrivateFlags & PFLAG_VISIBLE_HANDLER_IS_SET) != 0L) {
        node.visibleHandler(mVisibleHandler);
      }
      if ((mPrivateFlags & PFLAG_FOCUSED_HANDLER_IS_SET) != 0L) {
        node.focusedHandler(mFocusedHandler);
      }
      if ((mPrivateFlags & PFLAG_FULL_IMPRESSION_HANDLER_IS_SET) != 0L) {
        node.fullImpressionHandler(mFullImpressionHandler);
      }
      if ((mPrivateFlags & PFLAG_INVISIBLE_HANDLER_IS_SET) != 0L) {
        node.invisibleHandler(mInvisibleHandler);
      }
      if ((mPrivateFlags & PFLAG_UNFOCUSED_HANDLER_IS_SET) != 0L) {
        node.unfocusedHandler(mUnfocusedHandler);
      }
      if ((mPrivateFlags & PFLAG_TRANSITION_KEY_IS_SET) != 0L) {
        node.transitionKey(mTransitionKey);
      }
      if ((mPrivateFlags & PFLAG_VISIBLE_HEIGHT_RATIO_IS_SET) != 0L) {
        node.visibleHeightRatio(mVisibleHeightRatio);
      }
      if ((mPrivateFlags & PFLAG_VISIBLE_WIDTH_RATIO_IS_SET) != 0L) {
        node.visibleWidthRatio(mVisibleWidthRatio);
      }
      if ((mPrivateFlags & PFLAG_ALIGN_SELF_IS_SET) != 0L) {
        node.alignSelf(mAlignSelf);
      }
      if ((mPrivateFlags & PFLAG_POSITION_PERCENT_IS_SET) != 0L) {
        for (int i = 0; i < mPositionPercents.mNumEntries; i++) {
          node.positionPercent(mPositionPercents.mEdges[i], mPositionPercents.mValues[i]);
        }
      }
      if ((mPrivateFlags & PFLAG_FLEX_IS_SET) != 0L) {
        node.flex(mFlex);
      }
      if ((mPrivateFlags & PFLAG_FLEX_GROW_IS_SET) != 0L) {
        node.flexGrow(mFlexGrow);
      }
      if ((mPrivateFlags & PFLAG_FLEX_SHRINK_IS_SET) != 0L) {
        node.flexShrink(mFlexShrink);
      }
      if ((mPrivateFlags & PFLAG_FLEX_BASIS_IS_SET) != 0L) {
        node.flexBasisPx(mFlexBasisPx);
      }
      if ((mPrivateFlags & PFLAG_FLEX_BASIS_PERCENT_IS_SET) != 0L) {
        node.flexBasisPercent(mFlexBasisPercent);
      }
      if ((mPrivateFlags & PFLAG_WIDTH_PERCENT_IS_SET) != 0L) {
        node.widthPercent(mWidthPercent);
      }
      if ((mPrivateFlags & PFLAG_MIN_WIDTH_IS_SET) != 0L) {
        node.minWidthPx(mMinWidthPx);
      }
      if ((mPrivateFlags & PFLAG_MIN_WIDTH_PERCENT_IS_SET) != 0L) {
        node.minWidthPercent(mMinWidthPercent);
      }
      if ((mPrivateFlags & PFLAG_MAX_WIDTH_IS_SET) != 0L) {
        node.maxWidthPx(mMaxWidthPx);
      }
      if ((mPrivateFlags & PFLAG_MAX_WIDTH_PERCENT_IS_SET) != 0L) {
        node.maxWidthPercent(mMaxWidthPercent);
      }
      if ((mPrivateFlags & PFLAG_HEIGHT_PERCENT_IS_SET) != 0L) {
        node.heightPercent(mHeightPercent);
      }
      if ((mPrivateFlags & PFLAG_MIN_HEIGHT_IS_SET) != 0L) {
        node.minHeightPx(mMinHeightPx);
      }
      if ((mPrivateFlags & PFLAG_MIN_HEIGHT_PERCENT_IS_SET) != 0L) {
        node.minHeightPercent(mMinHeightPercent);
      }
      if ((mPrivateFlags & PFLAG_MAX_HEIGHT_IS_SET) != 0L) {
        node.maxHeightPx(mMaxHeightPx);
      }
      if ((mPrivateFlags & PFLAG_MAX_HEIGHT_PERCENT_IS_SET) != 0L) {
        node.maxHeightPercent(mMaxHeightPercent);
      }
      if ((mPrivateFlags & PFLAG_ASPECT_RATIO_IS_SET) != 0L) {
        node.aspectRatio(mAspectRatio);
      }
      if ((mPrivateFlags & PFLAG_MARGIN_IS_SET) != 0L) {
        for (int i = 0; i < mMargins.size(); i++) {
          node.marginPx(mMargins.getEdge(i), mMargins.getValue(i));
        }
      }
      if ((mPrivateFlags & PFLAG_MARGIN_PERCENT_IS_SET) != 0L) {
        for (int i = 0; i < mMarginPercents.mNumEntries; i++) {
          node.marginPercent(mMarginPercents.mEdges[i], mMarginPercents.mValues[i]);
        }
      }
      if ((mPrivateFlags & PFLAG_MARGIN_AUTO_IS_SET) != 0L) {
        for (YogaEdge edge : mMarginAutos) {
          node.marginAuto(edge);
        }
      }
      if ((mPrivateFlags & PFLAG_PADDING_IS_SET) != 0L) {
        for (int i = 0; i < mPaddings.size(); i++) {
          node.paddingPx(mPaddings.getEdge(i), mPaddings.getValue(i));
        }
      }
      if ((mPrivateFlags & PFLAG_PADDING_PERCENT_IS_SET) != 0L) {
        for (int i = 0; i < mPaddingPercents.mNumEntries; i++) {
          node.paddingPercent(mPaddingPercents.mEdges[i], mPaddingPercents.mValues[i]);
        }
      }
      if ((mPrivateFlags & PFLAG_TOUCH_EXPANSION_IS_SET) != 0L) {
        for (int i = 0; i < mTouchExpansions.size(); i++) {
          node.touchExpansionPx(mTouchExpansions.getEdge(i), mTouchExpansions.getValue(i));
        }
      }
      if ((mPrivateFlags & PFLAG_BORDER_IS_SET) != 0L) {
        node.border(mBorder);
      }
      if ((mPrivateFlags & PFLAG_STATE_LIST_ANIMATOR_IS_SET) != 0L) {
        node.stateListAnimator(mStateListAnimator);
      }
    }
  }

  static class YogaEdgesWithIntsImplOptimized implements YogaEdgesWithInts {

    private long mEdges;
    @Nullable private int[] mValues;

    @Override
    public void add(YogaEdge yogaEdge, int value) {
      int index = addEdge(yogaEdge);
      if (value != 0) {
        if (mValues == null) {
          mValues = createArray(Math.max(2, index + 1));
        } else if (index >= mValues.length) {
          int[] oldValues = mValues;
          mValues = new int[Math.min(oldValues.length * 2, YogaEdge.values().length)];
          System.arraycopy(oldValues, 0, mValues, 0, oldValues.length);
        }
        mValues[index] = value;
      }
    }

    private static int[] createArray(int size) {
      boolean myGK = false;
      if (myGK && size == 2) {
        return ArrayBatchAllocator.newArrayOfSize2(); // = new int[2];
      } else {
        return new int[size];
      }
    }

    private int addEdge(YogaEdge yogaEdge) {
      int size = lookup(0);
      insert(size + 1, yogaEdge.intValue());
      insert(0, size + 1);
      return size;
    }

    private int lookup(final int pos) {
      return (int) ((mEdges >> (pos * 4)) & 15);
    }

    private void insert(final int pos, final int val) {
      mEdges &= ~((long) (0xF) << (pos * 4));
      mEdges |= ((long) val << (pos * 4));
    }

    @Override
    public int size() {
      return lookup(0);
    }

    @Override
    public YogaEdge getEdge(int index) {
      return YogaEdge.fromInt(lookup(index + 1));
    }

    @Override
    public int getValue(int index) {
      return (mValues == null || mValues.length <= index) ? 0 : mValues[index];
    }
  }

  static class YogaEdgesWithIntsImpl implements YogaEdgesWithInts {
    YogaEdge[] mEdges = new YogaEdge[2];
    int[] mValues = new int[2];
    int mNumEntries;
    int mSize = 2;

    @Override
    public void add(YogaEdge yogaEdge, int value) {
      if (mNumEntries == mSize) {
        increaseSize();
      }

      mEdges[mNumEntries] = yogaEdge;
      mValues[mNumEntries] = value;
      mNumEntries++;
    }

    @Override
    public int size() {
      return mNumEntries;
    }

    @Override
    public YogaEdge getEdge(int index) {
      return mEdges[index];
    }

    @Override
    public int getValue(int index) {
      return mValues[index];
    }

    private void increaseSize() {
      YogaEdge[] oldEdges = mEdges;
      int[] oldValues = mValues;

      mSize *= 2;
      mEdges = new YogaEdge[mSize];
      mValues = new int[mSize];

      System.arraycopy(oldEdges, 0, mEdges, 0, mNumEntries);
      System.arraycopy(oldValues, 0, mValues, 0, mNumEntries);
    }
  }

  private static class YogaEdgesWithFloats {
    private YogaEdge[] mEdges = new YogaEdge[2];
    private float[] mValues = new float[2];
    private int mNumEntries;
    private int mSize = 2;

    private void add(YogaEdge yogaEdge, float value) {
      if (mNumEntries == mSize) {
        increaseSize();
      }

      mEdges[mNumEntries] = yogaEdge;
      mValues[mNumEntries] = value;
      mNumEntries++;
    }

    private void increaseSize() {
      YogaEdge[] oldEdges = mEdges;
      float[] oldValues = mValues;

      mSize *= 2;
      mEdges = new YogaEdge[mSize];
      mValues = new float[mSize];

      System.arraycopy(oldEdges, 0, mEdges, 0, mNumEntries);
      System.arraycopy(oldValues, 0, mValues, 0, mNumEntries);
    }
  }
}
