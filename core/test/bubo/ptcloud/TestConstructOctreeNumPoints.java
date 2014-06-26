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

package bubo.ptcloud;

import georegression.metric.Intersection3D_F64;
import georegression.struct.point.Point3D_F64;
import georegression.struct.shapes.Cube3D_F64;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author Peter Abeles
 */
public class TestConstructOctreeNumPoints {

	/**
	 * Makes sure the point and data are correctly associated to each other
	 */
	@Test
	public void addPoint_data() {
		Point3D_F64 p = new Point3D_F64(1,2,3);
		Integer d = 1;

		ConstructOctreeNumPoints alg = new ConstructOctreeNumPoints(10);
		alg.addPoint(p,d);

		Octree.Info a = alg.getTree().points.get(0);
		assertTrue(a.point==p);
		assertTrue(a.data == d );
	}

	@Test
	public void addPoint_singleNode() {
		ConstructOctreeNumPoints alg = new ConstructOctreeNumPoints(10);
		Octree tree = alg.getTree();
		tree.space = new Cube3D_F64(-50,-50,-50,100,100,100);
		tree.divider = new Point3D_F64();


		alg.addPoint(new Point3D_F64(1,1,1),null);

		assertEquals(1, tree.points.size());
	}

	@Test
	public void addPoint_multipleNodes() {
		ConstructOctreeNumPoints alg = new ConstructOctreeNumPoints(10);

		Point3D_F64 p = new Point3D_F64(1,1,1);

		Octree tree = alg.getTree();
		tree.space = new Cube3D_F64(-50,-50,-50,100,100,100);
		tree.divider = new Point3D_F64();
		tree.children = new Octree[8];

		Octree node = new Octree();
		tree.children[ tree.getChildIndex(p)] = node;

		alg.addPoint(p,null);

		assertEquals(1, tree.points.size());
		assertEquals(1, node.points.size());
	}

	@Test
	public void addPoint_createNode() {
		ConstructOctreeNumPoints alg = new ConstructOctreeNumPoints(10);

		Octree tree = alg.getTree();
		tree.space = new Cube3D_F64(-50,-50,-50,50,50,50);
		tree.divider = new Point3D_F64();

		for( int i = 0; i < 9; i++ ) {
			alg.addPoint(new Point3D_F64(1,1,1),null);
			alg.addPoint(new Point3D_F64(-1,-1,-1),null);
		}

		assertEquals(18, tree.points.size());
		assertFalse(tree.isLeaf());

		Octree node0 = tree.children[ tree.getChildIndex(new Point3D_F64(1,1,1))];
		Octree node1 = tree.children[ tree.getChildIndex(new Point3D_F64(-1,-1,-1))];

		assertTrue(node0 != node1 );

		assertEquals(9, node0.points.size());
		assertFalse(tree.isLeaf());
		assertEquals(9, node1.points.size());
		assertFalse(tree.isLeaf());

	}

	/**
	 * If more points have the same value then there is no good way to split the list.  They will go into the same
	 * bin and a naive algorithm will be stuck doing so for forever.
	 */
	@Test
	public void numerousIdenticalPoints() {
		ConstructOctreeNumPoints alg = new ConstructOctreeNumPoints(10);
		alg.initialize(new Cube3D_F64(-100,-100,-100,200,200,200));

		for( int i = 0; i < 100; i++ ) {
			Point3D_F64 a = new Point3D_F64(1,1,1);

			alg.addPoint(a,null);
		}

		// if there is no way to split the points then don't split the points
		Octree root = alg.getTree();
		assertTrue(root.isLeaf());

		// make sure all unused data was correctly reset
		assertEquals(1,alg.getAllNodes().size);
		for( int i = alg.storageInfo.size; i < alg.storageInfo.data.length; i++ ) {
			Octree.Info info = alg.storageInfo.data[i];

			assertTrue(info.point==null);
			assertTrue(info.data == null);
		}
		for( int i = alg.storageNodes.size; i < alg.storageNodes.data.length; i++ ) {
			Octree o = alg.storageNodes.data[i];

			assertTrue(o.children==null);
			assertTrue(o.parent==null);
			assertEquals(0, o.points.size());
		}

		for( int i = 0; i < alg.storageChildren.size(); i++ ) {
			Octree[] o = alg.storageChildren.get(i);

			for( int j = 0; j < o.length; j++ ) {
				assertTrue(o[j] == null);
			}
		}
	}

}
