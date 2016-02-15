package com.kodroid.pilot.lib.stack;

import junit.framework.TestCase;

import org.apache.commons.lang3.SerializationUtils;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.io.Serializable;

@RunWith(JUnit4.class)
public class PilotStackTest extends TestCase
{
    //[UnitOfWork_StateUnderTest_ExpectedBehavior]

    //==================================================================//
    // Instance method tests
    //==================================================================//

    @Test
    public void classComparison()
    {
        Assert.assertTrue(Object.class == Object.class);
    }

    public void getTopFrame_empty_shouldThrow()
    {
        Assert.assertNull(new PilotStack().getTopVisibleFrame());
    }

    @Test
    public void getTopFrame_oneFrameSameClass_shouldReturn()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestUIFrame1.class);
        PilotFrame testFrame = pilotStack.getTopVisibleFrame();
        Assert.assertNotNull(testFrame);
    }

    @Test
    public void getTopFrame_twoFrameOneUiOneData_shouldReturnUiFrame()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestUIFrame1.class);
        pilotStack.pushFrame(TestInvisibleDataFrame.class);
        PilotFrame testFrame = pilotStack.getTopVisibleFrame();
        Assert.assertNotNull(testFrame);
    }

    @Test
    public void getTopVisibleFrame_oneFrameNotVisible_shouldReturnNull()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestInvisibleDataFrame.class);
        PilotFrame returnedFrame = pilotStack.getTopVisibleFrame();
        Assert.assertNull(returnedFrame);
    }

    @Test
    public void popTopFrame_oneFrameSameInstance_shouldReturn()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestUIFrame1.class);
        pilotStack.popTopVisibleFrame();
        Assert.assertEquals(0, pilotStack.getFrameSize());
    }

    @Test(expected= IllegalStateException.class)
    public void popTopFrame_oneFrameDiffInstance_shouldThrow()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestUIFrame1.class);
        pilotStack.popTopVisibleFrame(new TestUIFrame1());
    }

    @Test(expected= IllegalStateException.class)
    public void popTopFrame_oneFrameDiffClass_shouldThrow()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestUIFrame1.class);
        pilotStack.popTopVisibleFrame(new TestUIFrame2());
    }

    @Test
    public void getScopedDateFrame_noDataFramesOnStack_shouldReturnNull()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestUIFrame1.class);
        PilotFrame returnedFrame = pilotStack.getFrameOfType(TestInvisibleDataFrame.class);
        Assert.assertNull(returnedFrame);
    }

    @Test
    public void getScopedDateFrame_oneDataFrameOnStack_shouldReturn()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestInvisibleDataFrame.class);
        PilotFrame returnedFrame = pilotStack.getFrameOfType(TestInvisibleDataFrame.class);
        Assert.assertNotNull(returnedFrame);
    }

    @Test
    public void popStackAtFrameType_threeFramesSameTypePopMiddleInclusive_listenerCalledWithFirstFrame()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestUIFrame1.class);
        pilotStack.pushFrame(TestUIFrame2.class);
        pilotStack.pushFrame(TestUIFrame3.class);

        //add listener
        PilotStack.TopFrameChangedListener mockedListener = Mockito.mock(PilotStack.TopFrameChangedListener.class);
        pilotStack.setTopFrameChangedListener(mockedListener);

        //perform pop
        pilotStack.popStackAtFrameType(TestUIFrame2.class, PilotStack.PopType.INCLUSIVE, true);

        //verify listener method called
        Mockito.verify(mockedListener).topVisibleFrameUpdated(
                Matchers.isA(TestUIFrame1.class),
                Matchers.eq(PilotStack.TopFrameChangedListener.Direction.BACK));

        Mockito.verifyNoMoreInteractions(mockedListener);
        Assert.assertEquals(1, pilotStack.getFrameSize());
    }

    @Test
    public void popStackAtFrameType_threeFramesSameTypePopBottomInclusive_listenerCalledEmpty()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestUIFrame1.class);
        pilotStack.pushFrame(TestUIFrame2.class);
        pilotStack.pushFrame(TestUIFrame3.class);

        //add listener
        PilotStack.StackEmptyListener mockedListener = Mockito.mock(PilotStack.StackEmptyListener.class);
        pilotStack.setStackEmptyListener(mockedListener);

        //perform pop
        pilotStack.popStackAtFrameType(TestUIFrame1.class, PilotStack.PopType.INCLUSIVE, true);

        //verify listener method called
        Mockito.verify(mockedListener).noVisibleFramesLeft();
        Mockito.verifyNoMoreInteractions(mockedListener);
        Assert.assertEquals(0, pilotStack.getFrameSize());
    }

    @Test
    public void popStackAtFrameType_threeFramesSameTypePopMiddleExclusive_listenerCalledWithSecondFrame()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestUIFrame1.class);
        pilotStack.pushFrame(TestUIFrame2.class);
        pilotStack.pushFrame(TestUIFrame3.class);

        //add listener
        PilotStack.TopFrameChangedListener mockedListener = Mockito.mock(PilotStack.TopFrameChangedListener.class);
        pilotStack.setTopFrameChangedListener(mockedListener);

        //perform pop
        pilotStack.popStackAtFrameType(TestUIFrame2.class, PilotStack.PopType.EXCLUSIVE, true);

        //verify listener method called
        Mockito.verify(mockedListener).topVisibleFrameUpdated(
                Matchers.isA(TestUIFrame2.class),
                Matchers.eq(PilotStack.TopFrameChangedListener.Direction.BACK));

        Mockito.verifyNoMoreInteractions(mockedListener);
        Assert.assertEquals(2, pilotStack.getFrameSize());
    }

    @Test
    public void popStackAtFrameType_threeFramesSameTypePopTopExclusive_listenerNotCalledAsNoChanges()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestUIFrame1.class);
        pilotStack.pushFrame(TestUIFrame2.class);
        pilotStack.pushFrame(TestUIFrame3.class);

        //add listener
        PilotStack.TopFrameChangedListener mockedListener = Mockito.mock(PilotStack.TopFrameChangedListener.class);
        pilotStack.setTopFrameChangedListener(mockedListener);

        //perform pop
        pilotStack.popStackAtFrameType(TestUIFrame3.class, PilotStack.PopType.EXCLUSIVE, true);

        //verify listener method called
        Mockito.verifyNoMoreInteractions(mockedListener);
        Assert.assertEquals(3, pilotStack.getFrameSize());
    }

    @Test
    public void removeThisFrame_threeFramesRemoveMiddle_listenerShouldBeRecalledWithTopFrame()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestUIFrame1.class);
        pilotStack.pushFrame(TestUIFrame2.class);
        TestUIFrame2 testUIFrame2 = (TestUIFrame2) pilotStack.getTopVisibleFrame();
        pilotStack.pushFrame(TestUIFrame3.class);

        //add listener
        PilotStack.TopFrameChangedListener mockedListener = Mockito.mock(PilotStack.TopFrameChangedListener.class);
        pilotStack.setTopFrameChangedListener(mockedListener);

        //perform pop
        pilotStack.removeFrame(testUIFrame2);

        //verify listener method called
        Mockito.verify(mockedListener).topVisibleFrameUpdated(
                Matchers.isA(TestUIFrame3.class),
                Matchers.eq(PilotStack.TopFrameChangedListener.Direction.BACK));
        Assert.assertEquals(2, pilotStack.getFrameSize());
    }

    @Test
    public void clearStack_shouldPop()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestUIFramePop.class);
        pilotStack.pushFrame(TestUIFramePop.class);
        TestUIFramePop middleFrame = (TestUIFramePop) pilotStack.getTopVisibleFrame();
        pilotStack.pushFrame(TestUIFramePop.class);
        pilotStack.clearStack(false);

        Assert.assertTrue(middleFrame.popped);
    }

    //==================================================================//
    // Listener Tests
    //==================================================================//

    @Test
    public void pushFrame_pushFirstUiFrame_listenerShouldBeCalled()
    {
        PilotStack pilotStack = new PilotStack();
        PilotStack.TopFrameChangedListener mockedListener = Mockito.mock(PilotStack.TopFrameChangedListener.class);
        pilotStack.setTopFrameChangedListener(mockedListener);
        pilotStack.pushFrame(TestUIFrame1.class);

        //verify listener method called
        Mockito.verify(mockedListener).topVisibleFrameUpdated(
                Matchers.isA(TestUIFrame1.class),
                Matchers.eq(PilotStack.TopFrameChangedListener.Direction.FORWARD));

        Mockito.verifyNoMoreInteractions(mockedListener);
    }

    @Test
    public void pushFrame_pushFirstDataFrame_listenerShouldNotBeCalled()
    {
        PilotStack pilotStack = new PilotStack();
        PilotStack.TopFrameChangedListener mockedListener = Mockito.mock(PilotStack.TopFrameChangedListener.class);
        pilotStack.setTopFrameChangedListener(mockedListener);
        pilotStack.pushFrame(TestInvisibleDataFrame.class);
        //verify listener method called
        Mockito.verifyNoMoreInteractions(mockedListener);
    }

    @Test
    public void popTopFrameObj_popFirstFrame_listenerShouldBeCalledWithNoUiFrames()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestUIFrame1.class);
        //add listener after push
        PilotStack.StackEmptyListener mockedListener = Mockito.mock(PilotStack.StackEmptyListener.class);
        pilotStack.setStackEmptyListener(mockedListener);
        pilotStack.popTopVisibleFrame();
        //verify
        Mockito.verify(mockedListener).noVisibleFramesLeft();
        Mockito.verifyNoMoreInteractions(mockedListener);
    }

    @Test
    public void popTopFrameObj_popUiFrameAboveDataFrame_listenerShouldBeCalledWithNoUiFrames()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestInvisibleDataFrame.class);
        pilotStack.pushFrame(TestUIFrame1.class);
        //add listener after push
        PilotStack.StackEmptyListener mockedListener = Mockito.mock(PilotStack.StackEmptyListener.class);
        pilotStack.setStackEmptyListener(mockedListener);
        pilotStack.popTopVisibleFrame();
        //verify
        Mockito.verify(mockedListener).noVisibleFramesLeft();
        Mockito.verifyNoMoreInteractions(mockedListener);
    }

    @Test
    public void popTopFrameObject_popUiFrameAboveDataAndUiFrame_listenerShouldBeCalledWithUiFrame()
    {
        PilotStack pilotStack = new PilotStack();

        pilotStack.pushFrame(TestUIFrame1.class);
        pilotStack.pushFrame(TestInvisibleDataFrame.class);
        pilotStack.pushFrame(TestUIFrame1.class);

        //add listener after push
        PilotStack.TopFrameChangedListener mockedListener = Mockito.mock(PilotStack.TopFrameChangedListener.class);
        pilotStack.setTopFrameChangedListener(mockedListener);
        pilotStack.popTopVisibleFrame();

        //verify
        Mockito.verify(mockedListener).topVisibleFrameUpdated(
                Matchers.isA(TestUIFrame1.class),
                Matchers.eq(PilotStack.TopFrameChangedListener.Direction.BACK));

        Mockito.verifyNoMoreInteractions(mockedListener);
    }

    @Test
    public void popTopFrameObject_popSecondFrame_listenerShouldBeCalledWithUiFrame()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(TestUIFrame1.class);
        pilotStack.pushFrame(TestUIFrame2.class);

        //add listener after push
        PilotStack.TopFrameChangedListener mockedListener = Mockito.mock(PilotStack.TopFrameChangedListener.class);
        pilotStack.setTopFrameChangedListener(mockedListener);
        pilotStack.popTopVisibleFrame();

        //verify
        Mockito.verify(mockedListener).topVisibleFrameUpdated(
                Matchers.isA(TestUIFrame1.class),
                Matchers.eq(PilotStack.TopFrameChangedListener.Direction.BACK));

        Mockito.verifyNoMoreInteractions(mockedListener);
    }

    @Test
    public void popTopFrameOfCategory_UiDataUiDataStackPoppingTopUi_topTwoFramesRemovedListenerShouldBeCalledWithBottomUiFrame()
    {
        PilotStack pilotStack = new PilotStack();

        pilotStack.pushFrame(TestUIFrame1.class);
        pilotStack.pushFrame(TestInvisibleDataFrame.class);
        pilotStack.pushFrame(TestUIFrame2.class);
        pilotStack.pushFrame(TestInvisibleDataFrame.class);
        Assert.assertEquals(4, pilotStack.getFrameSize());

        //add listener after push
        PilotStack.TopFrameChangedListener mockedListener = Mockito.mock(PilotStack.TopFrameChangedListener.class);
        pilotStack.setTopFrameChangedListener(mockedListener);
        pilotStack.popTopVisibleFrame();
        //verify
        Mockito.verify(mockedListener).topVisibleFrameUpdated(
                Matchers.isA(TestUIFrame1.class),
                Matchers.eq(PilotStack.TopFrameChangedListener.Direction.BACK));

        Mockito.verifyNoMoreInteractions(mockedListener);
        Assert.assertEquals(2, pilotStack.getFrameSize());
    }

    @Test
    public void popAtFrameType_UiDataUiStackPoppingTopData_topTwoFramesRemovedListenerShouldBeCalledWithBottomUiFrame()
    {
        PilotStack pilotStack = new PilotStack();

        pilotStack.pushFrame(TestUIFrame1.class);
        pilotStack.pushFrame(TestInvisibleDataFrame.class);
        pilotStack.pushFrame(TestUIFrame2.class);
        Assert.assertEquals(3, pilotStack.getFrameSize());

        //add listener after push
        PilotStack.TopFrameChangedListener mockedListener = Mockito.mock(PilotStack.TopFrameChangedListener.class);
        pilotStack.setTopFrameChangedListener(mockedListener);
        pilotStack.popStackAtFrameType(TestInvisibleDataFrame.class, PilotStack.PopType.INCLUSIVE, true);
        //verify
        Mockito.verify(mockedListener).topVisibleFrameUpdated(
                Matchers.isA(TestUIFrame1.class),
                Matchers.eq(PilotStack.TopFrameChangedListener.Direction.BACK));

        Mockito.verifyNoMoreInteractions(mockedListener);
        Assert.assertEquals(1, pilotStack.getFrameSize());
    }

    //==================================================================//
    // Push Stack Constructor Invocation Tests
    //==================================================================//

    @Test
    public void pushFrame_notPassingArgs_shouldBeFineWithNoArgsConstructor()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(NoArgsPilotFrame.class);
    }

    @Test
    public void pushFrame_passingArgs_shouldBeFineWithArgsConstructor()
    {
        PilotStack pilotStack = new PilotStack();
        Args args = new Args();
        pilotStack.pushFrame(ArgsPilotFrame.class, args);
        Assert.assertEquals(args, ((ArgsPilotFrame)pilotStack.getTopVisibleFrame()).args);
    }

    @Test(expected = RuntimeException.class)
    public void pushFrame_passingArgs_shouldFailAsOnlyNoArgsConstructor()
    {
        PilotStack pilotStack = new PilotStack();
        Args args = new Args();
        pilotStack.pushFrame(NoArgsPilotFrame.class, args);
    }

    @Test(expected = RuntimeException.class)
    public void pushFrame_notPassingArgs_shouldFailAsOnlyArgsConstructor()
    {
        PilotStack pilotStack = new PilotStack();
        pilotStack.pushFrame(ArgsPilotFrame.class);
    }

    //==================================================================//
    // Serializing Tests
    //==================================================================//

    //TODO

    //==================================================================//
    // Test Frames
    //==================================================================//

    public static class ArgsPilotFrame extends PilotFrame
    {
        private Args args;

        public ArgsPilotFrame(Args args) {
            super(args);
            this.args = args;
        }

        public Args getArgsTest() {
            return args;
        }
    }

    public static class NoArgsPilotFrame extends PilotFrame
    {
        public NoArgsPilotFrame()
        {
            super(null);
        }
    }

    //to test causing trouble
    public static class TestFrameNoType extends NoArgsPilotFrame
    {}

    public static class TestUIFrame1 extends NoArgsPilotFrame
    {}

    public static class TestUIFrame2 extends NoArgsPilotFrame
    {}

    public static class TestUIFrame3 extends NoArgsPilotFrame
    {}

    public static class TestUIFramePop extends NoArgsPilotFrame
    {
        private boolean popped;

        @Override
        public void popped() {
            popped = true;
        }
    }

    @InvisibleFrame
    public static class TestInvisibleDataFrame extends NoArgsPilotFrame
    {}
}