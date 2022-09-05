package io.geewit.core.utils.tree;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class Org extends SignedTreeNode<Org, Long> {
    private String name;

    @Override
    public Integer apply(Integer sign) {
        if (sign == null || sign == 0) {
            return super.sign == null ? 0 : super.sign;
        } else {
            return 1;
        }
    }

    @Override
    public void accept(Integer parentSign, Integer sign) {
        if (sign == null) {
            if (parentSign != null && (parentSign == 1 || parentSign == 2)) {
                super.sign = 1;
            } else {
                super.sign = 0;
            }
        } else if (sign == 1) {
            if (super.sign != null && super.sign == 2) {
                super.sign = 2;
            } else {
                super.sign = 1;
            }
        } else if (sign == 2) {
            super.sign = 2;
        } else {
            super.sign = 0;
        }
    }

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
