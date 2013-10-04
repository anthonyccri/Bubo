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

package bubo.ptcloud.wrapper;

import bubo.ptcloud.CloudShapeTypes;
import bubo.ptcloud.PointCloudShapeFinder;
import bubo.ptcloud.alg.*;
import georegression.geometry.UtilPoint3D_F64;
import georegression.struct.point.Point3D_F64;
import georegression.struct.shapes.Cube3D_F64;
import org.ddogleg.struct.FastQueue;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper which allows {@link bubo.ptcloud.alg.PointCloudShapeDetectionSchnabel2007} into
 * {@link PointCloudShapeFinder}.
 *
 * @author Peter Abeles
 */
public class Schnable2007_to_PointCloudShapeFinder implements PointCloudShapeFinder {

	ApproximateSurfaceNormals surfaceNormals;
	PointCloudShapeDetectionSchnabel2007 shapeDetector;
	List<CloudShapeTypes> shapeList;

	// Searches for duplicate shapes which describe the same set of points and merges them together
	MergeShapesPointVectorNN merge;

	List<FoundShape> mergeList = new ArrayList<FoundShape>();

	FastQueue<PointVectorNN> pointNormList = new FastQueue<PointVectorNN>(PointVectorNN.class,false);

	FastQueue<Shape> output = new FastQueue<Shape>(Shape.class,true);

	Cube3D_F64 boundingBox = new Cube3D_F64();

	List<PointVectorNN> unmatchePV = new ArrayList<PointVectorNN>();

	public Schnable2007_to_PointCloudShapeFinder(ApproximateSurfaceNormals surfaceNormals,
												 PointCloudShapeDetectionSchnabel2007 shapeDetector,
												 MergeShapesPointVectorNN merge,
												 List<CloudShapeTypes> shapeList ) {
		this.surfaceNormals = surfaceNormals;
		this.shapeDetector = shapeDetector;
		this.merge = merge;
		this.shapeList = shapeList;
	}

	@Override
	public void process(List<Point3D_F64> cloud , Cube3D_F64 boundingBox ) {
		pointNormList.reset();
		surfaceNormals.process(cloud,pointNormList);

		if( boundingBox == null ) {
			UtilPoint3D_F64.boundingCube(cloud,this.boundingBox);
		} else {
			this.boundingBox.set(boundingBox);
		}

		shapeDetector.process(pointNormList,this.boundingBox);

		mergeList.clear();
		mergeList.addAll(shapeDetector.getFoundObjects().toList());

		merge.merge(mergeList,cloud.size());

		convertIntoOuput(merge.getOutput());
	}

	/**
	 * Converts the list of shapes into the output format
	 */
	private void convertIntoOuput(List<FoundShape> schnabelShapes) {
		output.reset();
		for( int i = 0; i < schnabelShapes.size(); i++ ) {
			FoundShape fs = schnabelShapes.get(i);
			Shape os = output.grow();
			os.parameters = fs.modelParam;
			os.type = shapeList.get( fs.whichShape );
			os.points.clear();
			os.indexes.reset();

			// add the points to it
			for( int j = 0; j < fs.points.size(); j++ ) {
				PointVectorNN pv = fs.points.get(j);
				os.points.add(pv.p);
				os.indexes.add(pv.index);
			}
		}
	}

	@Override
	public List<Shape> getFound() {
		return output.toList();
	}

	@Override
	public void getUnmatched( List<Point3D_F64> unmatched ) {
		unmatchePV.clear();
		shapeDetector.findUnmatchedPoints(unmatchePV);

		for( int i = 0; i < unmatchePV.size(); i++ ) {
			unmatched.add( unmatchePV.get(i).p );
		}
	}

	@Override
	public List<CloudShapeTypes> getShapesList() {
		return shapeList;
	}
}
