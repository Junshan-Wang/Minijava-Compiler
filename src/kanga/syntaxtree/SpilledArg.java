//
// Generated by JTB 1.3.2
//

package kanga.syntaxtree;

import kanga.syntaxtree.*;
import kanga.visitor.*;
/**
 * Grammar production:
 * f0 -> "SPILLEDARG"
 * f1 -> IntegerLiteral()
 */
public class SpilledArg implements Node {
   public NodeToken f0;
   public IntegerLiteral f1;

   public SpilledArg(NodeToken n0, IntegerLiteral n1) {
      f0 = n0;
      f1 = n1;
   }

   public SpilledArg(IntegerLiteral n0) {
      f0 = new NodeToken("SPILLEDARG");
      f1 = n0;
   }

   public void accept(kanga.visitor.Visitor v) {
      v.visit(this);
   }
   public <R,A> R accept(kanga.visitor.GJVisitor<R,A> v, A argu) {
      return v.visit(this,argu);
   }
   public <R> R accept(kanga.visitor.GJNoArguVisitor<R> v) {
      return v.visit(this);
   }
   public <A> void accept(kanga.visitor.GJVoidVisitor<A> v, A argu) {
      v.visit(this,argu);
   }
}

