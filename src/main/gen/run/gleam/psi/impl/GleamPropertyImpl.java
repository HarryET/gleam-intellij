// This is a generated file. Not intended for manual editing.
package run.gleam.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static run.gleam.psi.GleamTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import run.gleam.psi.*;

public class GleamPropertyImpl extends ASTWrapperPsiElement implements GleamProperty {

  public GleamPropertyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull GleamVisitor visitor) {
    visitor.visitProperty(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GleamVisitor) accept((GleamVisitor)visitor);
    else super.accept(visitor);
  }

}
