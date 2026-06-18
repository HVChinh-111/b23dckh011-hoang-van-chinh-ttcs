package com.blog.demo.series.entity;

import com.blog.demo.common.entity.BaseEntity;
import com.blog.demo.post.entity.Post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A post placed inside a series at a given position (BR07.3). A post belongs to
 * at most one series (post_id is unique).
 */
@Entity
@Table(name = "series_post_item")
@Getter
@Setter
@NoArgsConstructor
public class SeriesPostItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "series_id", nullable = false)
    private Series series;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private Post post;

    @Column(name = "sequence_number", nullable = false)
    private int sequenceNumber;

    public SeriesPostItem(Series series, Post post, int sequenceNumber) {
        this.series = series;
        this.post = post;
        this.sequenceNumber = sequenceNumber;
    }
}
