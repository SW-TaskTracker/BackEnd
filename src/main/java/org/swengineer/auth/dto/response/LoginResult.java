package org.swengineer.auth.dto.response;

public record LoginResult(

        TokenResponse token, String nickname
)
{
}
