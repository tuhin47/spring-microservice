package me.tuhin47.entity.audit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.io.Serial;

@MappedSuperclass
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@JsonIgnoreProperties(
    value = {"createdBY", "updatedBy"},
    allowGetters = true
)
public abstract class UserDateAudit<U> extends DateAudit<U> {

    @Serial
    private static final long serialVersionUID = -954707305413700201L;

    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 36)
    private U createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 36)
    private U updatedBy;
}