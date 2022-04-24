// This is a generated file. Not intended for manual editing.
package run.gleam.lang.core.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import run.gleam.lang.core.psi.impl.*;

public interface GleamTypes {

  IElementType PROPERTY = new GleamElementType("PROPERTY");

  IElementType COMMENT = new GleamTokenType("COMMENT");
  IElementType CRLF = new GleamTokenType("CRLF");
  IElementType KEY = new GleamTokenType("KEY");
  IElementType SEPARATOR = new GleamTokenType("SEPARATOR");
  IElementType VALUE = new GleamTokenType("VALUE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == PROPERTY) {
        return new GleamPropertyImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
