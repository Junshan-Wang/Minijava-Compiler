//
// Generated by JTB 1.3.2
//

package minijava.syntaxtree;

import minijava.syntaxtree.Node;
import minijava.syntaxtree.NodeToken;

/**
 * Grammar production:
 * f0 -> ","
 * f1 -> Expression()
 */
public class ExpressionRest implements Node {
   public NodeToken f0;
   public Expression f1;

   public ExpressionRest(NodeToken n0, Expression n1) {
      f0 = n0;
      f1 = n1;
   }

   public ExpressionRest(Expression n0) {
      f0 = new NodeToken(",");
      f1 = n0;
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

