/*
 *  Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.alibaba.fescar.server.lock;

import com.alibaba.fescar.core.model.BranchType;
import com.alibaba.fescar.server.UUIDGenerator;
import com.alibaba.fescar.server.session.BranchSession;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author zhimo.xiao@gmail.com
 * @since 2019/1/23
 */

public class DefaultLockManagerImplTest {

    private LockManager lockManager = new DefaultLockManagerImpl();

    private static final long transactionIdOne = UUIDGenerator.generateUUID();

    private static final long transactionIdTwo = UUIDGenerator.generateUUID();

    private static final String resourceId = "tb_1";

    private static final String lockKey = "tb_1:11";

    @Test(dataProvider = "branchSessionProvider")
    public void acquireLockTest(BranchSession branchSession) throws Exception {
        branchSession.setTransactionId(transactionIdOne);
        boolean resultOne = lockManager.acquireLock(branchSession);
        Assert.assertTrue(resultOne);
        branchSession.unlock();

        branchSession.setTransactionId(transactionIdTwo);
        boolean resultTwo = lockManager.acquireLock(branchSession);
        Assert.assertFalse(resultTwo);
        branchSession.unlock();
    }

    @Test(dependsOnMethods = "acquireLockTest")
    public void isLockableTest() throws Exception {
        boolean resultOne = lockManager.isLockable(transactionIdOne, resourceId, lockKey);
        Assert.assertTrue(resultOne);

        boolean resultTwo = lockManager.isLockable(transactionIdTwo, resourceId, lockKey);
        Assert.assertFalse(resultTwo);
    }

    @DataProvider
    public static Object[][] branchSessionProvider() {
        BranchSession branchSession = new BranchSession();
        branchSession.setBranchId(1L);
        branchSession.setClientId("c1");
        branchSession.setResourceGroupId("my_test_tx_group");
        branchSession.setResourceId("tb_1");
        branchSession.setLockKey(lockKey);
        branchSession.setBranchType(BranchType.AT);
        branchSession.setApplicationId("demo-child-app");
        branchSession.setTxServiceGroup("my_test_tx_group");
        branchSession.setApplicationData("{\"data\":\"test\"}");
        branchSession.setBranchType(BranchType.AT);
        return new Object[][]{{branchSession}};
    }

}
