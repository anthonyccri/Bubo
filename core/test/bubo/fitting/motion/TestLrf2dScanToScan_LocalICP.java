/*
 * Copyright (c) 2013, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Project BUBO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bubo.fitting.motion;

import bubo.fitting.StoppingCondition;


/**
 * @author Peter Abeles
 */
public class TestLrf2dScanToScan_LocalICP extends StandardTestsScanToScan {
    public TestLrf2dScanToScan_LocalICP() {
        angTol = 0.02;
        tranTol = 0.02;
    }

    @Override
    public Lrf2dScanToScan createAlg() {
        StoppingCondition stop = new StoppingCondition(20,0.0001);

        return new Lrf2dScanToScan_LocalICP(stop,50,0.2);
    }
}
