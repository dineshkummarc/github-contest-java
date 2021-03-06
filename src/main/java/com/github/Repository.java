/**
 * Copyright 2009 Kevin J. Menard Jr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.github;

import java.util.*;

public class Repository
{
  public final String id;
  public final String owner;
  public final String name;
  public final String created_at;

  public Repository parent;

  public Set<Repository> children = new HashSet<Repository>();
  public Set<Watcher> watchers = new HashSet<Watcher>();
  public NeighborRegion region;

  public Repository(final String id, final String owner, final String name, final String created_at)
  {
    this.id = id;
    this.owner = owner;
    this.name = name;
    this.created_at = created_at;
  }

  public Repository(final String id, final String combined_name, final String created_at)
  {
    final String split_name[] = combined_name.split("/");

    this.id = id;
    this.owner = split_name[0];
    this.name = split_name[1];
    this.created_at = created_at;
  }

  public void setParent(final Repository parent)
  {
    this.parent = parent;

    if (parent != null)
    {
      parent.children.add(this);
    }
  }

  @Override
  public String toString()
  {
    final StringBuilder ret = new StringBuilder(String.format("%s:%s/%s,%s", id, owner, name, created_at));

    if (parent != null)
    {
      ret.append(String.format(",%s", parent.id));
    }

    return ret.toString();
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    Repository that = (Repository) o;

    if (created_at != null ? !created_at.equals(that.created_at) : that.created_at != null)
    {
      return false;
    }
    if (id != null ? !id.equals(that.id) : that.id != null)
    {
      return false;
    }
    if (name != null ? !name.equals(that.name) : that.name != null)
    {
      return false;
    }
    if (owner != null ? !owner.equals(that.owner) : that.owner != null)
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (owner != null ? owner.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (created_at != null ? created_at.hashCode() : 0);
    return result;
  }

  public boolean isRelated(final Repository second)
  {
    final Repository root = Repository.findRoot(this);

    final Queue<Repository> queue = new LinkedList<Repository>(Arrays.asList(root));

    while (!queue.isEmpty())
    {
      final Repository repo = queue.poll();
      if (repo.equals(second))
      {
        return true;
      }

      queue.addAll(repo.children);
    }

    return false;
  }

  protected static Repository findRoot(final Repository repository)
  {
    return repository.parent == null ? repository : findRoot(repository.parent);
  }

  public void associate(final Watcher watcher)
  {
    watchers.add(watcher);

    watcher.repositories.add(this);
  }
}
