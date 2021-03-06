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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

import java.util.HashSet;
import java.util.Arrays;
import java.util.List;

@Test
public class RepositoryTest
{
  private Repository test_repo;

  @BeforeMethod
  public void setUp()
  {
    this.test_repo = new Repository("1234", "user_a/yo", "2009-02-26");
  }

  public void test_new()
  {
    String id = "1234";
    String owner = "user_a";
    String name = "yo";
    String created_at = "2009-02-26";

    Repository r = new Repository(id, owner, name, created_at);

    assertEquals(r.id, id);
    assertEquals(r.owner, owner);
    assertEquals(r.name, name);
    assertEquals(r.created_at, created_at);
  }

  public void test_set_parent()
  {
    Repository parent = new Repository("1234", "user_a/yo", "2009-02-26");
    Repository child = new Repository("2345", "user_b/yo", "2009-03-16");

    child.setParent(parent);

    assertEquals(parent, child.parent);
    assertEquals(new HashSet<Repository>(Arrays.asList(child)), parent.children);
    assertTrue(child.children.isEmpty());
  }

  public void test_equality()
  {
    Repository a = new Repository("1234", "user_a/yo", "2009-02-26");
    Repository b = new Repository("1234", "user_a/yo", "2009-02-26");
    Repository c = new Repository("1235", "user_a/yo2", "2009-02-26");

    // Two repositories with the same name, creation time, and parent should be equal.
    assertEquals(a, b);
    assertEquals(b, a);
    assertEquals(a.hashCode(), b.hashCode());


    assertFalse(a.equals(c));
    assertFalse(b.equals(c));
  }

  public void test_to_s()
  {
    assertEquals("1234:user_a/yo,2009-02-26", test_repo.toString());

    Repository with_parent = new Repository("2356", "user_b/yo", "2009-03-21");
    with_parent.setParent(test_repo);

    assertEquals("2356:user_b/yo,2009-03-21,1234", with_parent.toString());
  }

  public void test_related()
  {
    Repository parent = new Repository("12341", "user_a", "yo", "2009-02-26");
    Repository child = new Repository("2345", "user_b", "yo", "2009-03-16");
    Repository grandchild_a = new Repository("6790", "user_c", "yo", "2009-05-08");
    Repository grandchild_b = new Repository("2368", "user_d", "yo", "2009-05-09");

    Repository non_related = new Repository("367734", "user_q", "not_related", "2009-07-02");

    // Establish family bond.
    child.setParent(parent);
    grandchild_a.setParent(child);
    grandchild_b.setParent(child);

    List<Repository> repositories = Arrays.asList(parent, child, grandchild_a, grandchild_b);

    for (Repository first : repositories)
    {
      // Make sure all the various players in the family tree are related.
      for (Repository second : repositories)
      {
        assertTrue(first.isRelated(second));
        assertTrue(second.isRelated(first));
      }

      assertFalse(first.isRelated(non_related));
    }
  }

  public void test_associate()
  {
    Watcher watcher = new Watcher("1");
    Repository repo = new Repository("1234", "user_a", "yo", "1234");

    repo.associate(watcher);

    // Check that bi-directional mappings are set up.
    assertEquals(Arrays.asList(watcher), repo.watchers);
    assertEquals(Arrays.asList(repo), watcher.repositories);
  }
}