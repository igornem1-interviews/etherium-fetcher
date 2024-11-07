package limechain.etherium.fetcher.config.db;

import java.lang.reflect.AnnotatedElement;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration;
import org.springframework.transaction.interceptor.DelegatingTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

@Configuration
public class TransactionManagementConfiguration extends ProxyTransactionManagementConfiguration {

    @SuppressWarnings("serial")
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TransactionAttributeSource transactionAttributeSource() {
        return new AnnotationTransactionAttributeSource() {

            @Nullable
            protected TransactionAttribute determineTransactionAttribute(AnnotatedElement element) {
                TransactionAttribute ta = super.determineTransactionAttribute(element);
                if (ta == null) {
                    return null;
                } else {
                    return new DelegatingTransactionAttribute(ta) {
                        @Override
                        public boolean rollbackOn(Throwable ex) {
                            return super.rollbackOn(ex) || ex instanceof Exception;
                        }
                    };
                }
            }
        };
    }
}
