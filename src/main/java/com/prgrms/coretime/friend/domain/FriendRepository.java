package com.prgrms.coretime.friend.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRepository extends JpaRepository<Friend, FriendId> {

  @Query(
      value = "select * from friend where followee_id=:id "
          + "MINUS "
          + "select f1.followee_id as followee_id, f1.follower_id as follower_id, f1.created_at as created_at, f1.updated_at as updated_at from friend as f1 inner join friend as f2 on f1.followee_id=f2.follower_id where f1.followee_id=:id and f1.follower_id=f2.followee_id",
      nativeQuery = true,
      countQuery = "select count(*) from "
          + "(select * from friend where followee_id=:id "
          + "MINUS "
          + "select f1.followee_id as followee_id, f1.follower_id as follower_id, f1.created_at as created_at, f1.updated_at as updated_at from friend as f1 inner join friend as f2 on f1.followee_id=f2.follower_id where f1.followee_id=:id and f1.follower_id=f2.followee_id)"
  )
  Page<Friend> findByFolloweeUser_Id(@Param("id") Long followeeId, Pageable pageable);

  @Query(
      value = "select f1.follower_id, f1.followee_id, f1.created_at, f1.updated_at from friend as f1 inner join friend as f2 on f1.follower_id=f2.followee_id where f1.follower_id=:id and f1.followee_id=f2.follower_id",
      nativeQuery = true,
      countQuery = "select count(*) from friend as f1 inner join friend as f2 on f1.follower_id=f2.followee_id where f1.follower_id=:id and f1.followee_id=f2.follower_id")
  Page<Friend> findAllFriendWithPaging(@Param("id") Long id, Pageable pageable);

  @Query(
      value =
          "SELECT EXISTS (SELECT * FROM friend WHERE followee_id=:first_id AND follower_id=:second_id) "
              + "AND "
              + "EXISTS (SELECT * FROM friend WHERE followee_id=:second_id AND follower_id=:first_id)",
      nativeQuery = true)
  boolean existsFriendRelationship(@Param("first_id") Long firstId,
      @Param("second_id") Long secondId);
}
