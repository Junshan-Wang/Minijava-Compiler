//
// Generated by JTB 1.3.2
//

package minijava.syntaxtree;

import minijava.syntaxtree.Node;
import minijava.syntaxtree.NodeToken;

/**
 * Grammar production:
 * f0 -> PrimaryExpression()
 * f1 -> "*"
 * f2 -> PrimaryExpression()
 */
public class TimesExpression implements Node {
   public PrimaryExpression f0;
   public NodeToken f1;
   public PrimaryExpression f2;

   public TimesExpression(PrimaryExpression n0, NodeToken n1, PrimaryExpression n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
   }

   public TimesExpression(PrimaryExpression n0, PrimaryExpression n1) {
      f0 = n0;
      f1 = new NodeToken("*");
      f2 = n1;
   }

   public void accept(minijava.visitor.Visitor v) {
      v.visit(this);
   }
   public <R,A> R accept(minijava.visitor.GJVisitor<R,A> v, A argu) {
      return v.visit(this,argu);
   }
   public <R> R accept(minijava.visitor.GJNoArguVisitor<R> v) {
      return v.visit(this);
   }
   public <A> void accept(minijava.visitor.GJVoidVisitor<A> v, A argu) {
      v.visit(this,argu);
   }
}

