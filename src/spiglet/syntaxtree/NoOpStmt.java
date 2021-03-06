//
// Generated by JTB 1.3.2
//

package spiglet.syntaxtree;

import spiglet.visitor.*;
import spiglet.syntaxtree.*;

/**
 * Grammar production:
 * f0 -> "NOOP"
 */
public class NoOpStmt implements Node {
   public NodeToken f0;

   public NoOpStmt(NodeToken n0) {
      f0 = n0;
   }

   public NoOpStmt() {
      f0 = new NodeToken("NOOP");
   }

   public void accept(spiglet.visitor.Visitor v) {
      v.visit(this);
   }
   public <R,A> R accept(spiglet.visitor.GJVisitor<R,A> v, A argu) {
      return v.visit(this,argu);
   }
   public <R> R accept(spiglet.visitor.GJNoArguVisitor<R> v) {
      return v.visit(this);
   }
   public <A> void accept(spiglet.visitor.GJVoidVisitor<A> v, A argu) {
      v.visit(this,argu);
   }
}

