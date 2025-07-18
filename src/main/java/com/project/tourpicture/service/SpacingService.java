package com.project.tourpicture.service;

import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.stereotype.Service;
import scala.collection.Seq;

import java.util.List;

@Service
public class SpacingService {

    public String spacingWord(String input) {
        //정규화
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(input);

        //토큰화
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);

        //토큰에서 문자열만 추출
        List<String> tokenStrings = OpenKoreanTextProcessorJava.tokensToJavaStringList(tokens);

        //문자열 토큰을 공백으로 join하여 띄어쓰기 적용
        return String.join(" ", tokenStrings);
    }
}
