package io.geewit.core.utils.tree;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
@Getter
@Builder
public class SignParams {

    /**
     * parentSign 父节点sign
     */
    private Integer parentSign;

    /**
     * 传入的sign
     */
    private Integer sign;
}
