package pt.isel.daw.battleship

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.daw.battleship.http.pipeline.AuthenticationInterceptor
import pt.isel.daw.battleship.http.pipeline.UserArgumentResolver
import pt.isel.daw.battleship.repository.jdbi.configure
import pt.isel.daw.battleship.utils.Sha256TokenEncoder
import pt.isel.daw.battleship.utils.StringHelper

@SpringBootApplication
class BattleshipApplication {

	@Bean
	fun jdbi() = Jdbi.create(
		PGSimpleDataSource().apply {
			setURL(StringHelper.getDatabaseString())
		}
	).configure()

	@Bean
	fun passwordEncoder() = BCryptPasswordEncoder()

	@Bean
	fun tokenEncoder() = Sha256TokenEncoder()
}

@Configuration
class PipelineConfigurer(
	val authenticationInterceptor: AuthenticationInterceptor,
	val userArgumentResolver: UserArgumentResolver,
) : WebMvcConfigurer {

	override fun addInterceptors(registry: InterceptorRegistry) {
		registry.addInterceptor(authenticationInterceptor)
	}

	override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
		resolvers.add(userArgumentResolver)
	}
}

fun main(args: Array<String>) {
	runApplication<BattleshipApplication>(*args)
}
