package soya.framework.transform.schema;

import java.io.IOException;

public interface KnowledgeTreeBase<T, K, N> extends KnowledgeBase<T, KnowledgeTree<K, N>> {

    interface KnowledgeTreeBaseBuilder<T, K, N> extends T123W.KnowledgeBuilder<T, KnowledgeTree<K, N>> {

        KnowledgeTreeBaseBuilder<T, K, N> knowledgeExtractor(KnowledgeExtractor<K> knowledgeExtractor);

        KnowledgeTreeBaseBuilder<T, K, N> knowledgeDigester(KnowledgeDigester<K, N> digester);
    }

    interface KnowledgeExtractor<K> {
        KnowledgeExtractor<K> source(Object src);

        K extract() throws IOException;
    }

    interface KnowledgeDigester<K, N> {
        KnowledgeTree<K, N> digester(K knowledge);
    }
}
