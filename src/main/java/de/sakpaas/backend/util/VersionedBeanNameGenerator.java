package de.sakpaas.backend.util;

import java.util.Arrays;
import java.util.Optional;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;

@Data
@NoArgsConstructor
public class VersionedBeanNameGenerator implements BeanNameGenerator {

  public static final String BASE_PACKAGE = "de.sakpaas.backend";
  public static final BeanNameGenerator DEFAULT_BEAN_NAME_GENERATOR =
      new DefaultBeanNameGenerator();

  @Override
  public String generateBeanName(BeanDefinition beanDefinition,
                                 BeanDefinitionRegistry beanDefinitionRegistry) {
    if (!beanDefinition.getBeanClassName().startsWith(BASE_PACKAGE)) {
      return DEFAULT_BEAN_NAME_GENERATOR
          .generateBeanName(beanDefinition, beanDefinitionRegistry);
    }

    String[] split = beanDefinition.getBeanClassName().split("\\.");
    Optional<String> bean = Arrays.stream(split).reduce((first, second) -> second);
    Optional<String> version = Arrays.stream(split)
        .filter(s -> s.matches("^v[0-9]+$"))
        .map(String::toUpperCase)
        .findAny();
    return bean.get() + version.orElse("");
  }
}