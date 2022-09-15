/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package core.framework.javaagent.instrumentation;

import static java.util.Collections.singletonList;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers;
import java.util.List;
import net.bytebuddy.matcher.ElementMatcher;

/** @author ebin */
@AutoService(InstrumentationModule.class)
public final class Servlet3InstrumentationModule extends InstrumentationModule {
  public Servlet3InstrumentationModule() {
    super("servlet-demo", "servlet-3");
  }

  /*
  We want this instrumentation to be applied after the standard servlet instrumentation.
  The latter creates a server span around http request.
  This instrumentation needs access to that server span.
   */
  @Override
  public int order() {
    return 1;
  }

  @Override
  public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
    return AgentElementMatchers.hasClassesNamed("javax.servlet.http.HttpServlet");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return singletonList(new Servlet3Instrumentation());
  }
}
