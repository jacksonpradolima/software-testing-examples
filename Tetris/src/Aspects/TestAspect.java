/* Description: A Test Aspect that shows that the images 
 * are only loaded once and that the caching works.
 * 
 * 
 * Copyright 2003 Gustav Evertsson All Rights Reserved.
*/
package Aspects;

public aspect TestAspect {
	
	pointcut logPoint(String fileName) : call(* Logic.TetrisImages.loadImage(String)) && args(fileName);
	
	before(String fileName) : logPoint(fileName) {
		System.out.println(thisJoinPoint.getSignature() + ", " + fileName);
	}
	
}
