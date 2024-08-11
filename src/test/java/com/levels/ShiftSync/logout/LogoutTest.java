package com.levels.ShiftSync.logout;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class LogoutTest {

    @Autowired
    private MockMvc mockMvc;

    /*　要約
     * ログイン後にログアウトを行い、ログインの指定のURL遷移とセッション情報削除ができているかを確認。
     * */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testLogout() throws Exception {
        // Perform logout request
        mockMvc.perform(MockMvcRequestBuilders.post("/logout")
                .with(csrf())) // CSRF トークンを追加
            // Verify redirection to login page with logout query parameter
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.redirectedUrl("/login?logout"));

        /* テストコードの挙動メモ
         * mockMvc.perform(MockMvcRequestBuilders.get("/login").with(csrf())) で、ログインページへの新しいリクエストを送信しています。
         * .andExpect(MockMvcResultMatchers.status().isOk()) で、ログインページが正常に表示されることを確認しています。
         * .andExpect(MockMvcResultMatchers.cookie().doesNotExist("JSESSIONID")) で、JSESSIONID クッキーが存在しないことを確認しています。これは、セッションが無効化されていることを示しています。
         * */
        mockMvc.perform(MockMvcRequestBuilders.get("/login")
                .with(csrf())) // CSRF トークンを追加
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.cookie().doesNotExist("JSESSIONID"));
    }

}
