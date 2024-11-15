package com.hwan.patchhub.controller;

import com.hwan.patchhub.common.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = Constants.API_PREFIX)
@RequiredArgsConstructor
public class PatchHubController {
}
