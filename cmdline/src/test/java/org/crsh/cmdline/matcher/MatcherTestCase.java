/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.crsh.cmdline.matcher;

import junit.framework.TestCase;
import org.crsh.cmdline.annotations.Argument;
import org.crsh.cmdline.ClassDescriptor;
import org.crsh.cmdline.CommandFactory;
import org.crsh.cmdline.annotations.Command;
import org.crsh.cmdline.annotations.Option;
import org.crsh.cmdline.annotations.Required;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MatcherTestCase extends TestCase {


  public void testRequiredClassOption() throws Exception {
    class A {
      @Option(names = "o")
      @Required
      String s;
    }
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> analyzer = desc.matcher();

    A a = new A();
    analyzer.match("-o foo").invoke(a);
    assertEquals("foo", a.s);

    try {
      a = new A();
      analyzer.match("").invoke(a);
      fail();
    }
    catch (CmdSyntaxException e) {
    }
  }

  public void testOptionalClassOption() throws Exception {
    class A {
      @Option(names = "o")
      String s;
    }
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> analyzer = desc.matcher();

    A a = new A();
    analyzer.match("-o foo").invoke(a);
    assertEquals("foo", a.s);

    a = new A();
    analyzer.match("").invoke(a);
    assertEquals(null, a.s);
  }

  public void testPrimitiveClassArgument() throws Exception {
    class A {
      @Argument
      int i;
    }
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> analyzer = desc.matcher();

    A a = new A();
    analyzer.match("5").invoke(a);
    assertEquals(5, a.i);

    a = new A();
    analyzer.match("5 6").invoke(a);
    assertEquals(5, a.i);

    a = new A();
    a.i = -3;
    analyzer.match("").invoke(a);
    assertEquals(-3, a.i);
  }

  public static class PMA {
    int i;
    @Command
    public void m(@Argument int i) {
      this.i = i;
    }
  }

  public void testPrimitiveMethodArgument() throws Exception {
    ClassDescriptor<PMA> desc = CommandFactory.DEFAULT.create(PMA.class);
    Matcher<PMA> analyzer = desc.matcher();

    PMA a = new PMA();
    analyzer.match("m 5").invoke(a);
    assertEquals(5, a.i);

    a = new PMA();
    analyzer.match("m 5 6").invoke(a);
    assertEquals(5, a.i);

    a = new PMA();
    try {
      analyzer.match("m").invoke(a);
      fail();
    }
    catch (CmdSyntaxException e) {
    }
  }

  public void testOptionalClassArgument() throws Exception {
    class A {
      @Argument
      String s;
    }
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> analyzer = desc.matcher();

    A a = new A();
    analyzer.match("foo").invoke(a);
    assertEquals("foo", a.s);

    a = new A();
    analyzer.match("foo bar").invoke(a);
    assertEquals("foo", a.s);

    a = new A();
    analyzer.match("").invoke(a);
    assertEquals(null, a.s);
  }

  public static class BC {
    @Argument
    List<String> s;
    @Command
    public void bar(@Argument List<String> s) { this.s = s; }
  }

  public void testOptionalArgumentList() throws Exception {
    ClassDescriptor<BC> desc = CommandFactory.DEFAULT.create(BC.class);
    Matcher<BC> analyzer = desc.matcher();

    for (String s : Arrays.asList("", "bar ")) {
      BC a = new BC();
      analyzer.match(s + "").invoke(a);
      assertEquals(null, a.s);

      a = new BC();
      analyzer.match(s + "foo").invoke(a);
      assertEquals(Arrays.asList("foo"), a.s);

      a = new BC();
      analyzer.match(s + "foo bar").invoke(a);
      assertEquals(Arrays.asList("foo", "bar"), a.s);

      a = new BC();
      analyzer.match(s + "foo ").invoke(a);
      assertEquals(Arrays.asList("foo"), a.s);
    }
  }

  public void testRequiredArgumentList() throws Exception {
    class A {
      @Argument
      @Required
      List<String> s;
    }
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> analyzer = desc.matcher();

    A a = new A();
    try {
      analyzer.match("").invoke(a);
      fail();
    }
    catch (CmdSyntaxException expected) {
    }

    a = new A();
    analyzer.match("foo").invoke(a);
    assertEquals(Arrays.asList("foo"), a.s);

    a = new A();
    analyzer.match("foo bar").invoke(a);
    assertEquals(Arrays.asList("foo", "bar"), a.s);
  }

  public static class A {
    @Option(names = "s")
    String s;
    @Command
    public void m(@Option(names = "o") String o, @Argument String a) {
      this.o = o;
      this.a = a;
    }
    String o;
    String a;
  }

  public void testMethodInvocation() throws Exception {

    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> analyzer = desc.matcher();

    //
    A a = new A();
    analyzer.match("-s foo m -o bar juu").invoke(a);
    assertEquals("foo", a.s);
    assertEquals("bar", a.o);
    assertEquals("juu", a.a);

    //
    a = new A();
    analyzer.match("m -o bar juu").invoke(a);
    assertEquals(null, a.s);
    assertEquals("bar", a.o);
    assertEquals("juu", a.a);

    //
    a = new A();
    analyzer.match("m juu").invoke(a);
    assertEquals(null, a.s);
    assertEquals(null, a.o);
    assertEquals("juu", a.a);

    //
    a = new A();
    analyzer.match("m -o bar").invoke(a);
    assertEquals(null, a.s);
    assertEquals("bar", a.o);
    assertEquals(null, a.a);

    a = new A();
    analyzer.match("m").invoke(a);
    assertEquals(null, a.s);
    assertEquals(null, a.o);
    assertEquals(null, a.a);
  }

  public static class B {

    int count;

    @Command
    public void main() {
      count++;
    }
  }

  public void testMainMethodInvocation() throws Exception {
    ClassDescriptor<B> desc = CommandFactory.DEFAULT.create(B.class);
    Matcher<B> analyzer = desc.matcher("main");

    //
    B b = new B();
    analyzer.match("").invoke(b);
    assertEquals(1, b.count);
  }

  public static class C {

    Locale locale;

    @Command
    public void main(Locale locale) {
      this.locale = locale;
    }
  }

  public void testInvocationAttributeInjection() throws Exception {

    ClassDescriptor<C> desc = CommandFactory.DEFAULT.create(C.class);
    Matcher<C> analyzer = desc.matcher("main");

    //
    C c = new C();
    Resolver context = new Resolver() {
      public <T> T resolve(Class<T> type) {
        if (type.equals(Locale.class)) {
          return type.cast(Locale.FRENCH);
        } else {
          return null;
        }
      }
    };
    analyzer.match("").invoke(context, c);
    assertEquals(Locale.FRENCH, c.locale);
  }

  public static class D {

    private Integer i;

    @Command
    public void a(@Option(names = "o") Integer i) {
      this.i = i;
    }

    @Command
    public void b(@Option(names = "o") int i) {
      this.i = i;
    }
  }

  public void testInvocationTypeConversionInjection() throws Exception {

    ClassDescriptor<D> desc = CommandFactory.DEFAULT.create(D.class);

    //
    D d = new D();
    desc.matcher("a").match("-o 5").invoke(d);
    assertEquals((Integer)5, d.i);

    //
    d = new D();
    desc.matcher("b").match("-o 5").invoke(d);
    assertEquals((Integer)5, d.i);
  }

  public static class E {

    private String i;

    @Command
    public void a(@Option(names = "o", unquote = false) String i) {
      this.i = i;
    }
  }

  public void testQuoted() throws Exception {

    ClassDescriptor<E> desc = CommandFactory.DEFAULT.create(E.class);

    //
    E e = new E();
    desc.matcher("a").match("-o a").invoke(e);
    assertEquals("a", e.i);

    //
    e = new E();
    desc.matcher("a").match("-o \"a\"").invoke(e);
    assertEquals("\"a\"", e.i);
  }

  public static class F {
    List<String> s;
    @Command
    public void foo(@Option(names = "o") List<String> s) { this.s = s; }
  }

  public void testOptionList() throws Exception {

    ClassDescriptor<F> desc = CommandFactory.DEFAULT.create(F.class);

    //
    F f = new F();
    desc.matcher("foo").match("-o a").invoke(f);
    assertEquals(Arrays.asList("a"), f.s);

    //
    f = new F();
    desc.matcher("foo").match("-o a -o b").invoke(f);
    assertEquals(Arrays.asList("a", "b"), f.s);
  }


  public static class G {
    Custom o;
    @Command
    public void foo(@Option(names = "o") Custom o) { this.o = o; }
  }

  public void testValue() throws Exception {

    //
    ClassDescriptor<G> desc = new CommandFactory(MatcherTestCase.class.getClassLoader()).create(G.class);

    //
    G g = new G();
    desc.matcher("foo").match("-o a").invoke(g);
    assertEquals(new Custom("a"), g.o);
  }

  public static class H {
    @Command
    public void foo()  throws Exception { throw new Exception("fooexception"); }
  }

  public void testException() throws Exception {

    ClassDescriptor<H> desc = CommandFactory.DEFAULT.create(H.class);

    //
    H h = new H();
    CommandMatch<H, ?, ?> match = desc.matcher("foo").match("");
    try {
      match.invoke(h);
      fail();
    } catch (CmdLineException e) {
      assertEquals(Exception.class, e.getCause().getClass());
      assertEquals("fooexception", e.getCause().getMessage());
    }
  }

  public static class I {
    @Command
    public void foo() { throw new RuntimeException("fooruntimeexception"); }
  }

  public void testRuntimeException() throws Exception {

    ClassDescriptor<I> desc = CommandFactory.DEFAULT.create(I.class);

    //
    I i = new I();
    CommandMatch<I, ?, ?> match = desc.matcher("foo").match("");
    try {
      match.invoke(i);
      fail();
    } catch (CmdLineException e) {
      assertEquals(RuntimeException.class, e.getCause().getClass());
      assertEquals("fooruntimeexception", e.getCause().getMessage());
    }
  }

  public static class J {
    @Command
    public void foo() { throw new Error("fooerror"); }
  }

  public void testError() throws Exception {

    ClassDescriptor<J> desc = CommandFactory.DEFAULT.create(J.class);

    //
    J j = new J();
    CommandMatch<J, ?, ?> match = desc.matcher("foo").match("");
    try {
      match.invoke(j);
      fail();
    } catch (Error e) {
      assertEquals("fooerror", e.getMessage());
    }
  }

  public void testBooleanParameter() throws Exception {
    class A {
      @Option(names = "o")
      boolean o;
    }
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> analyzer = desc.matcher();

    //
    A a = new A();
    analyzer.match("-o").invoke(a);
    assertEquals(true, a.o);
  }

  public void testSCP() throws Exception {
    class SCP {
      @Option(names = "t")
      boolean t;
      @Argument
      @Required
      String target;
    }
    ClassDescriptor<SCP> desc = CommandFactory.DEFAULT.create(SCP.class);
    Matcher<SCP> analyzer = desc.matcher();

    //
    SCP scp = new SCP();
    CommandMatch<SCP, ?, ?> matcher = analyzer.match("-t -- portal:collaboration:/Documents");
    matcher.invoke(scp);
    assertEquals(true, scp.t);
    assertEquals("portal:collaboration:/Documents", scp.target);

    //
    scp = new SCP();
    matcher = analyzer.match("-t");
    try {
      matcher.invoke(scp);
      fail();
    } catch (CmdLineException e) {
    }
  }
}
