package io.geewit.utils.core.tree;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Setter
@Getter
@ToString
public class Org extends SignedTreeNode<Org, Long> {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Org)) {
            return false;
        }
        Org org = (Org) o;
        return Objects.equals(this.id, org.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
