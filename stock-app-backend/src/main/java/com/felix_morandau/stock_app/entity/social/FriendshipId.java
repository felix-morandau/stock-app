package com.felix_morandau.stock_app.entity.social;

import com.felix_morandau.stock_app.entity.transactional.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@Check(constraints = "user_one_id <> user_two_id")
public class FriendshipId implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_one_id")
    private User userOne;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_two_id")
    private User userTwo;
}
