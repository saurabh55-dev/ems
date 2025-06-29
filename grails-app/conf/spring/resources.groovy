import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

beans = {
    passwordEncoder(BCryptPasswordEncoder)
    scheduledAnnotationBeanPostProcessor(ScheduledAnnotationBeanPostProcessor)
}