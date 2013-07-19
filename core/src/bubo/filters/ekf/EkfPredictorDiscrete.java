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

package bubo.filters.ekf;

import org.ejml.data.DenseMatrix64F;

/**
 * State predictor for an EKF which assumes a constant time period between updates.
 *
 * @author Peter Abeles
 */
public interface EkfPredictorDiscrete extends EkfPredictor {
	/**
	 * Before any of the other functions are called this needs to be called first.
	 * It tells the propagator to compute matrices for a time step
	 */
	public void compute(DenseMatrix64F mean);
}
