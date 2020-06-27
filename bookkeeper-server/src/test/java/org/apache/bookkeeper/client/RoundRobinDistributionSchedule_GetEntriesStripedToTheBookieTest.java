package org.apache.bookkeeper.client;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;

@RunWith(Parameterized.class)
public class RoundRobinDistributionSchedule_GetEntriesStripedToTheBookieTest {

    private final int bookieIndex;
    private final long startEntryId;
    private final long lastEntryId;
    private final int writeQuorumSize;
    private final int ackQuorumSize; //only used in constructor, not in tested method
    private final int ensembleSize;
    private final Boolean[] result;
    private final int cardinality;

    @Parameters
    public static Collection<Object[]> getParameters(){
        Integer[] bIndex = {-1, 0, 2};
        Long[] startId = {-1L, 0L, 1L};
        Long[] lastId = {-1L, 0L, 1L, 3L};
        Integer[] writeQuorum = {2, 3};
        int ackQuorum = 3;
        Integer[] ensemble = {0, 3, 4};
        Integer[] card = {0, 2, 3};

        return Arrays.asList(new Object[][]{
                {bIndex[1], startId[1], lastId[3], writeQuorum[0], ackQuorum, ensemble[1], card[2], new Boolean[] {true, false, true, true}},
                {bIndex[2], startId[1], lastId[3], writeQuorum[0], ackQuorum, ensemble[1], card[1], new Boolean[] {false, true, true, false}},
                {bIndex[1], startId[2], lastId[2], writeQuorum[0], ackQuorum, ensemble[1], card[0], new Boolean[] {false}},
                //should throw exception (startId<0)
                {bIndex[2], startId[0], lastId[1], writeQuorum[0], ackQuorum, ensemble[1], card[2], new Boolean[] {}},
                //should throw exception (bIndex<0)
                {bIndex[0], startId[1], lastId[3], writeQuorum[0], ackQuorum, ensemble[1], card[2], new Boolean[] {}},
                //should throw exception (lastId<startId)
                {bIndex[1], startId[2], lastId[1], writeQuorum[0], ackQuorum, ensemble[1], card[2], new Boolean[] {}},
                //should throw exception (ensemble<1)
                {bIndex[1], startId[2], lastId[2], writeQuorum[0], ackQuorum, ensemble[0], card[0], new Boolean[] {}},
                //added to increase condition coverage, but seems useless...
                //should throw exception (lastId<0)
                {bIndex[1], startId[2], lastId[0], writeQuorum[0], ackQuorum, ensemble[1], card[0], new Boolean[] {}},
                //added to increase condition coverage
                {bIndex[2], startId[1], lastId[3], writeQuorum[1], ackQuorum, ensemble[2], card[2], new Boolean[] {true, true, true, false}},
                //added to increase mutation coverage, kills 2 mutants
                {bIndex[1], startId[2], lastId[3], writeQuorum[0], ackQuorum, ensemble[1], card[1], new Boolean[] {false, true, true}}
        });
    }

    public RoundRobinDistributionSchedule_GetEntriesStripedToTheBookieTest(int bookieIndex, long startEntryId, long lastEntryId,
                                                                           Integer writeQuorumSize, Integer ackQuorumSize,
                                                                           Integer ensembleSize, Integer cardinality, Boolean[] result){
        this.bookieIndex = bookieIndex;
        this.startEntryId = startEntryId;
        this.lastEntryId = lastEntryId;
        this.writeQuorumSize = writeQuorumSize;
        this.ackQuorumSize = ackQuorumSize;
        this.ensembleSize = ensembleSize;
        this.cardinality = cardinality;
        this.result = result;
    }

    @Test
    public void testGetStripedToTheBookie(){
        RoundRobinDistributionSchedule schedule = new RoundRobinDistributionSchedule(writeQuorumSize, ackQuorumSize, ensembleSize);
        try {
            BitSet entries = schedule.getEntriesStripedToTheBookie(bookieIndex, startEntryId, lastEntryId);
            Assert.assertEquals(cardinality, entries.cardinality());
            for (int i = 0; i < entries.length(); i++) {
                Assert.assertEquals(result[i], entries.get(i));
            }
        } catch (IllegalArgumentException e){
            Assert.assertTrue(startEntryId==-1L || bookieIndex==-1 || lastEntryId==0 || ensembleSize==0 || lastEntryId==-1L);
        } catch (IndexOutOfBoundsException e){
            Assert.fail("Exception not thrown");
        }
    }
}