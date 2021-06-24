package com.wezik.app.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NamedQuery(
    name="FullLog.countFlagged", query = "SELECT COUNT(*) FROM FullLog WHERE flagged=1"
)

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "logs")
@EqualsAndHashCode
public class FullLog {

    @Id
    private String id;

    @Column
    private String type;

    @Column
    private String host;

    @Column
    private Long time;

    @Column
    private boolean flagged;

}
