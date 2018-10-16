package com.howtographql.hackernews.models;

import java.util.List;

public class Feed {
  private final long count;
  private final List<Link> links;

  public Feed(List<Link> links, long count) {
    this.count = count;
    this.links = links;
  }

  /**
   * @return the count
   */
  public long getCount() {
    return count;
  }

  /**
   * @return the links
   */
  public List<Link> getLinks() {
    return links;
  }
}