package org.apache.bcel.generic;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache BCEL" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache BCEL", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.*;
import java.util.StringTokenizer;

import adaptj_pool.util.MethodEntity;

/**
 * Super class for the INVOKExxx family of instructions.
 *
 * @version $Id: InvokeInstruction.java,v 1.1 2004/03/17 23:08:58 cgoard Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public abstract class InvokeInstruction extends FieldOrMethod
  implements ExceptionThrower, TypedInstruction, StackConsumer, StackProducer {

  /* ------------------------------------------------------------------------ *
   * The following code was added by Bruno Dufour for the AdaptJ project      *
   * ------------------------------------------------------------------------ */

  private MethodEntity method_entity;

  public MethodEntity getMethodEntity() {
      return this.method_entity;
  }

  public void setMethodEntity(MethodEntity me) {
      this.method_entity = me;
  }

  /* ------------------------------------------------------------------------ *
   *  End code modifications by Bruno Dufour                                  *
   * ------------------------------------------------------------------------ */  


      
  /**
   * Empty constructor needed for the Class.newInstance() statement in
   * Instruction.readInstruction(). Not to be used otherwise.
   */
  InvokeInstruction() {}

  /**
   * @param index to constant pool
   */
  protected InvokeInstruction(short opcode, int index) {
    super(opcode, index);
  }

  /**
   * @return mnemonic for instruction with symbolic references resolved
   */
  public String toString(ConstantPool cp) {
    Constant        c   = cp.getConstant(index);
    StringTokenizer tok = new StringTokenizer(cp.constantToString(c));

    return Constants.OPCODE_NAMES[opcode] + " " +
      tok.nextToken().replace('.', '/') + tok.nextToken();
  }

  /**
   * Also works for instructions whose stack effect depends on the
   * constant pool entry they reference.
   * @return Number of words consumed from stack by this instruction
   */
  public int consumeStack(ConstantPoolGen cpg) {
    return consumeStack(cpg.getConstantPool());
  }
  
  /**
   * Also works for instructions whose stack effect depends on the
   * constant pool entry they reference.
   * @return Number of words consumed from stack by this instruction
   */
  public int consumeStack(ConstantPool cp) {
      String signature = getSignature(cp);
      Type[] args      = Type.getArgumentTypes(signature);
      int    sum;

      if(opcode == Constants.INVOKESTATIC)
	sum = 0;
      else
	sum = 1;  // this reference

      int n = args.length;
      for (int i = 0; i < n; i++)
	sum += args[i].getSize();

      return sum;
   }
  
  /**
   * Also works for instructions whose stack effect depends on the
   * constant pool entry they reference.
   * @return Number of words produced onto stack by this instruction
   */
  public int produceStack(ConstantPoolGen cpg) {
    return produceStack(cpg.getConstantPool());
  }
  
  /**
   * Also works for instructions whose stack effect depends on the
   * constant pool entry they reference.
   * @return Number of words produced onto stack by this instruction
   */
  public int produceStack(ConstantPool cp) {
    return getReturnType(cp).getSize();
  }

  /** @return return type of referenced method.
   */
  public Type getType(ConstantPoolGen cpg) {
    return getType(cpg.getConstantPool());
  }
  
  /** @return return type of referenced method.
   */
  public Type getType(ConstantPool cp) {
    return getReturnType(cp);
  }

  /** @return name of referenced method.
   */
  public String getMethodName(ConstantPoolGen cpg) {
    return getMethodName(cpg.getConstantPool());
  }

  /** @return name of referenced method.
   */
  public String getMethodName(ConstantPool cp) {
    return getName(cp);
  }

  /** @return return type of referenced method.
   */
  public Type getReturnType(ConstantPoolGen cpg) {
    return getReturnType(cpg.getConstantPool());
  }

  /** @return return type of referenced method.
   */
  public Type getReturnType(ConstantPool cp) {
    return Type.getReturnType(getSignature(cp));
  }

  /** @return argument types of referenced method.
   */
  public Type[] getArgumentTypes(ConstantPoolGen cpg) {
    return getArgumentTypes(cpg.getConstantPool());
  }

  /** @return argument types of referenced method.
   */
  public Type[] getArgumentTypes(ConstantPool cp) {
    return Type.getArgumentTypes(getSignature(cp));
  }
}
