package io.geewit.core.utils.tree;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class Org extends TreeNode<Org, Long> {
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Org)) return false;
        Org org = (Org) o;
        return Objects.equals(this.id, org.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
