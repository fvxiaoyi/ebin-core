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
public final class DispatcherServletInstrumentationModule extends InstrumentationModule {
  public DispatcherServletInstrumentationModule() {
    super("spring-dispatcher-servlet", "process-dispatch");
  }

  @Override
  public int order() {
    return 1;
  }

  @Override
  public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
    return AgentElementMatchers.hasClassesNamed(
        "org.springframework.web.servlet.DispatcherServlet");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return singletonList(new DispatcherServletInstrumentation());
  }
}
