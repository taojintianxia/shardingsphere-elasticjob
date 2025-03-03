/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.elasticjob.tracing.rdb.storage;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.shardingsphere.elasticjob.tracing.event.JobExecutionEvent;
import org.apache.shardingsphere.elasticjob.tracing.event.JobStatusTraceEvent;
import org.apache.shardingsphere.elasticjob.tracing.event.JobStatusTraceEvent.State;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RDBJobEventStorageTest {
    
    private RDBJobEventStorage storage;
    
    private BasicDataSource dataSource;
    
    @BeforeEach
    void setup() throws SQLException {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(org.h2.Driver.class.getName());
        dataSource.setUrl("jdbc:h2:mem:job_event_storage");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        storage = RDBJobEventStorage.getInstance(dataSource);
    }
    
    @AfterEach
    void teardown() throws SQLException {
        dataSource.close();
    }
    
    @Test
    void assertAddJobExecutionEvent() {
        assertTrue(storage.addJobExecutionEvent(new JobExecutionEvent("localhost", "127.0.0.1", "fake_task_id", "test_job", JobExecutionEvent.ExecutionSource.NORMAL_TRIGGER, 0)));
    }
    
    @Test
    void assertAddJobStatusTraceEvent() {
        assertTrue(storage.addJobStatusTraceEvent(
                new JobStatusTraceEvent("test_job", "fake_task_id", "fake_slave_id", "READY", "0", State.TASK_RUNNING, "message is empty.")));
    }
    
    @Test
    void assertAddJobStatusTraceEventWhenFailoverWithTaskStagingState() {
        JobStatusTraceEvent jobStatusTraceEvent = new JobStatusTraceEvent(
                "test_job", "fake_failover_task_id", "fake_slave_id", "FAILOVER", "0", State.TASK_STAGING, "message is empty.");
        jobStatusTraceEvent.setOriginalTaskId("original_fake_failover_task_id");
        assertThat(storage.getJobStatusTraceEvents("fake_failover_task_id").size(), is(0));
        storage.addJobStatusTraceEvent(jobStatusTraceEvent);
        assertThat(storage.getJobStatusTraceEvents("fake_failover_task_id").size(), is(1));
    }
    
    @Test
    void assertAddJobStatusTraceEventWhenFailoverWithTaskFailedState() {
        JobStatusTraceEvent stagingJobStatusTraceEvent = new JobStatusTraceEvent(
                "test_job", "fake_failed_failover_task_id", "fake_slave_id", "FAILOVER", "0", State.TASK_STAGING, "message is empty.");
        stagingJobStatusTraceEvent.setOriginalTaskId("original_fake_failed_failover_task_id");
        storage.addJobStatusTraceEvent(stagingJobStatusTraceEvent);
        JobStatusTraceEvent failedJobStatusTraceEvent = new JobStatusTraceEvent(
                "test_job", "fake_failed_failover_task_id", "fake_slave_id", "FAILOVER", "0", State.TASK_FAILED, "message is empty.");
        storage.addJobStatusTraceEvent(failedJobStatusTraceEvent);
        List<JobStatusTraceEvent> jobStatusTraceEvents = storage.getJobStatusTraceEvents("fake_failed_failover_task_id");
        assertThat(jobStatusTraceEvents.size(), is(2));
        for (JobStatusTraceEvent jobStatusTraceEvent : jobStatusTraceEvents) {
            assertThat(jobStatusTraceEvent.getOriginalTaskId(), is("original_fake_failed_failover_task_id"));
        }
    }
    
    @Test
    void assertUpdateJobExecutionEventWhenSuccess() {
        JobExecutionEvent startEvent = new JobExecutionEvent("localhost", "127.0.0.1", "fake_task_id", "test_job", JobExecutionEvent.ExecutionSource.NORMAL_TRIGGER, 0);
        assertTrue(storage.addJobExecutionEvent(startEvent));
        JobExecutionEvent successEvent = startEvent.executionSuccess();
        assertTrue(storage.addJobExecutionEvent(successEvent));
    }
    
    @Test
    void assertUpdateJobExecutionEventWhenFailure() {
        JobExecutionEvent startEvent = new JobExecutionEvent("localhost", "127.0.0.1", "fake_task_id", "test_job", JobExecutionEvent.ExecutionSource.NORMAL_TRIGGER, 0);
        assertTrue(storage.addJobExecutionEvent(startEvent));
        JobExecutionEvent failureEvent = startEvent.executionFailure("java.lang.RuntimeException: failure");
        assertTrue(storage.addJobExecutionEvent(failureEvent));
        assertThat(failureEvent.getFailureCause(), is("java.lang.RuntimeException: failure"));
        assertNotNull(failureEvent.getCompleteTime());
    }
    
    @Test
    void assertUpdateJobExecutionEventWhenSuccessAndConflict() {
        JobExecutionEvent startEvent = new JobExecutionEvent("localhost", "127.0.0.1", "fake_task_id", "test_job", JobExecutionEvent.ExecutionSource.NORMAL_TRIGGER, 0);
        JobExecutionEvent successEvent = startEvent.executionSuccess();
        assertTrue(storage.addJobExecutionEvent(successEvent));
        assertFalse(storage.addJobExecutionEvent(startEvent));
    }
    
    @Test
    void assertUpdateJobExecutionEventWhenFailureAndConflict() {
        JobExecutionEvent startEvent = new JobExecutionEvent("localhost", "127.0.0.1", "fake_task_id", "test_job", JobExecutionEvent.ExecutionSource.NORMAL_TRIGGER, 0);
        JobExecutionEvent failureEvent = startEvent.executionFailure("java.lang.RuntimeException: failure");
        assertTrue(storage.addJobExecutionEvent(failureEvent));
        assertThat(failureEvent.getFailureCause(), is("java.lang.RuntimeException: failure"));
        assertFalse(storage.addJobExecutionEvent(startEvent));
    }
    
    @Test
    void assertUpdateJobExecutionEventWhenFailureAndMessageExceed() {
        JobExecutionEvent startEvent = new JobExecutionEvent("localhost", "127.0.0.1", "fake_task_id", "test_job", JobExecutionEvent.ExecutionSource.NORMAL_TRIGGER, 0);
        assertTrue(storage.addJobExecutionEvent(startEvent));
        StringBuilder failureMsg = new StringBuilder();
        for (int i = 0; i < 600; i++) {
            failureMsg.append(i);
        }
        JobExecutionEvent failEvent = startEvent.executionFailure("java.lang.RuntimeException: failure" + failureMsg);
        assertTrue(storage.addJobExecutionEvent(failEvent));
        assertThat(failEvent.getFailureCause(), startsWith("java.lang.RuntimeException: failure"));
    }
    
    @Test
    void assertFindJobExecutionEvent() {
        storage.addJobExecutionEvent(new JobExecutionEvent("localhost", "127.0.0.1", "fake_task_id", "test_job", JobExecutionEvent.ExecutionSource.NORMAL_TRIGGER, 0));
    }
}
