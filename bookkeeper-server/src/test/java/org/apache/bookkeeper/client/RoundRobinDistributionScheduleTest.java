package org.apache.bookkeeper.client;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class RoundRobinDistributionScheduleTest {

    private final DistributionSchedule.WriteSet writeSet;
    private final Integer[] positions;
    private final Integer[] result;

    @Parameters
    public static Collection<Integer[][]> getParameters(){
        Integer[] caller1 = new Integer[]{-2, -1 ,0 ,1 ,2};
        Integer[] pos1 = new Integer[]{3, 1};
        Integer[] pos2 = new Integer[]{1, 3};
        Integer[] pos3 = new Integer[]{0, 4};
        Integer[] pos4 = new Integer[]{1, 1};

        return Arrays.asList(new Integer[][][]{
                {caller1, pos1, new Integer[]{-2, 1, -1, 0, 2}},
                {caller1, pos2, new Integer[]{-2, 0, 1, -1, 2}},
                {caller1, pos3, new Integer[]{-1, 0, 1, 2, -2}},
                {caller1, pos4, new Integer[]{-2, -1, 0, 1, 2}}
        });
    }

    public RoundRobinDistributionScheduleTest(Integer[] caller, Integer[] positions, Integer[] result){
        this.writeSet = RoundRobinDistributionSchedule.writeSetFromValues(caller);
        this.positions = positions;
        this.result = result;
    }

    @Test
    public void testMoveAndShift(){
        writeSet.moveAndShift(positions[0], positions[1]);
        Assert.assertEquals(writeSet, RoundRobinDistributionSchedule.writeSetFromValues(result));
    }
}