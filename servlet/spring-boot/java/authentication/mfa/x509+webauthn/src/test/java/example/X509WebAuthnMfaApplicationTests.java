/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package example;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.core.GrantedAuthorities.FACTOR_WEBAUTHN_AUTHORITY;
import static org.springframework.security.core.GrantedAuthorities.FACTOR_X509_AUTHORITY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Rob Winch
 */
@SpringBootTest
@AutoConfigureMockMvc
public class X509WebAuthnMfaApplicationTests {
	@Autowired
	private MockMvc mvc;

	@Test
	void indexWhenUnauthenticatedThenRedirectsToLogin() throws Exception {
		this.mvc.perform(get("/"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("http://localhost/login"));
	}

	@Test
	@WithMockUser
	void indexWhenAuthenticatedButNoFactorsThenRedirectsToLogin() throws Exception {
		this.mvc.perform(get("/"))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(authorities = FACTOR_WEBAUTHN_AUTHORITY)
	void indexWhenAuthenticatedWithWebAuthnThenForbidden() throws Exception {
		this.mvc.perform(get("/"))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(authorities = FACTOR_X509_AUTHORITY)
	void indexWhenAuthenticatedWithX509ThenRedirectsToWebAuthn() throws Exception {
		this.mvc.perform(get("/"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("http://localhost/login?factor=webauthn"));
	}

}
