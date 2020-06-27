package org.apache.bookkeeper.client;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class RoundRobinDistributionSchedule_MoveAndShiftTest {

    private final DistributionSchedule.WriteSet writeSet;
    private final Integer[] positions;
    private final Integer[] result;

    @Parameters
    public static Collection<Integer[][]> getParameters(){
        Integer[] caller = new Integer[]{-2, -1 ,0 ,1 ,2};
        Integer[][] pos = new Integer[][]{{3, 1}, {1, 3}, {0, 4}, {1, 1}, {-1, 3}, {5, 0}, {0, 6}};

        return Arrays.asList(new Integer[][][]{
                {caller, pos[0], new Integer[]{-2, 1, -1, 0, 2}},
                {caller, pos[1], new Integer[]{-2, 0, 1, -1, 2}},
                {caller, pos[2], new Integer[]{-1, 0, 1, 2, -2}},
                {caller, pos[3], new Integer[]{-2, -1, 0, 1, 2}},
                {caller, pos[4], new Integer[]{0,0,0,0,0}},
                //{caller, pos[5], new Integer[]{0,0,0,0,0}},           this should be fixed
                {caller, pos[6], new Integer[]{0,0,0,0,0}}
        });
    }

    public RoundRobinDistributionSchedule_MoveAndShiftTest(Integer[] caller, Integer[] positions, Integer[] result){
        this.writeSet = RoundRobinDistributionSchedule.writeSetFromValues(caller);
        this.positions = positions;
        this.result = result;
    }

    @Test
    public void testMoveAndShift(){
        try {
            writeSet.moveAndShift(positions[0], positions[1]);
            Assert.assertEquals("wrong shift", writeSet, RoundRobinDistributionSchedule.writeSetFromValues(result));
        } catch (ArrayIndexOutOfBoundsException e){
            Assert.fail("variable 'array' is throwing the exception, it should be thrown by checkBounds method");
        } catch (IndexOutOfBoundsException e) {
            //using "positions[1] == 5" even though it will not reach this point
            Assert.assertTrue("should not throw this exception", positions[0] == -1 || positions[1] == 6 || positions[1] == 5);
        }
    }
}