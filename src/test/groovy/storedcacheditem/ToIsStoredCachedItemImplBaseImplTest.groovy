package storedcacheditem

import com.google.protobuf.ByteString
import id.id.IsId
import id.output.IsOutput
import io.grpc.StatusRuntimeException
import io.grpc.inprocess.InProcessChannelBuilder
import main.Test
import removedcacheditem.input.IsInput
import removedcacheditem.removedcacheditem.NotRemovedCachedItem
import removedcacheditem.removedcacheditem.ToIsRemovedCachedItemGrpc
import retrievedcacheditem.retrievedcacheditem.ToIsRetrievedCachedItemGrpc
import spock.lang.Shared
import spock.lang.Specification
import storedcacheditem.storedcacheditem.NotStoredCachedItem
import storedcacheditem.storedcacheditem.ToIsStoredCachedItemGrpc

import java.util.concurrent.TimeUnit

class ToIsStoredCachedItemImplBaseImplTest extends Specification {


    @Shared
    def retrieve

    @Shared
    def store

    @Shared
    def remove

    def setupSpec() {
        Test.before()
        store = ToIsStoredCachedItemGrpc.newBlockingStub(InProcessChannelBuilder.forName(ToIsStoredCachedItemGrpc.SERVICE_NAME).usePlaintext().build()).withDeadlineAfter(1, TimeUnit.MINUTES).withWaitForReady()
        retrieve = ToIsRetrievedCachedItemGrpc.newBlockingStub(InProcessChannelBuilder.forName(ToIsRetrievedCachedItemGrpc.SERVICE_NAME).usePlaintext().build()).withDeadlineAfter(1, TimeUnit.MINUTES).withWaitForReady()
        remove = ToIsRemovedCachedItemGrpc.newBlockingStub(InProcessChannelBuilder.forName(ToIsRemovedCachedItemGrpc.SERVICE_NAME).usePlaintext().build()).withDeadlineAfter(1, TimeUnit.MINUTES).withWaitForReady()
    }


    def """Should not allow empty"""() {

        setup:
        def request = NotStoredCachedItem.newBuilder().build()

        when:
        store.produce(request)

        then:
        thrown StatusRuntimeException
    }


    def """Should not allow empty key"""() {
        setup:
        def request = NotStoredCachedItem.newBuilder()
                .setIsInput(storedcacheditem.input.IsInput.newBuilder()
                        .setIsId(IsId.newBuilder()
                                .setIsOutput(IsOutput.newBuilder()
                                        .build())
                                .build())
                        .build())
                .build()

        when:
        store.produce(request)

        then:
        def exception = thrown StatusRuntimeException
        exception.message == "INVALID_ARGUMENT: 422"
    }

    def """Should throw an error on existing key"""() {
        def key = System.currentTimeMillis()
        setup:
        def request = NotStoredCachedItem.newBuilder()
                .setIsInput(storedcacheditem.input.IsInput.newBuilder()
                        .setIsId(IsId.newBuilder()
                                .setIsOutput(IsOutput.newBuilder()
                                        .setIsStringValue(String.valueOf(key))
                                        .build())
                                .build())
                        .setIsUseLockBoolean(true)
                        .setIsItemBytes(ByteString.copyFrom(String.valueOf(key % 1000), "UTF-8"))
                        .build())
                .build()

        when:
        store.produce(request)
        store.produce(request)

        then:
        def exception = thrown StatusRuntimeException
        exception.message == "INVALID_ARGUMENT: 409"
    }


    def """Should not fail on non existing key"""() {

        setup:
        def key = System.currentTimeMillis()
        def request = NotStoredCachedItem.newBuilder()
                .setIsInput(storedcacheditem.input.IsInput.newBuilder()
                        .setIsId(IsId.newBuilder()
                                .setIsOutput(IsOutput.newBuilder()
                                        .setIsStringValue(String.valueOf(key))
                                        .build())
                                .build())
                        .setIsUseLockBoolean(true)
                        .setIsItemBytes(ByteString.copyFrom(String.valueOf(key % 1000), "UTF-8"))
                        .build())
                .build()

        when:
        store.produce(request)

        then:
        notThrown StatusRuntimeException

    }

    def """Should store non existing key"""() {

        setup:
        def key = System.currentTimeMillis()
        def storeRequest = NotStoredCachedItem.newBuilder()
                .setIsInput(storedcacheditem.input.IsInput.newBuilder()
                        .setIsId(IsId.newBuilder()
                                .setIsOutput(IsOutput.newBuilder()
                                        .setIsStringValue(String.valueOf(key))
                                        .build())
                                .build())
                        .setIsUseLockBoolean(true)
                        .setIsItemBytes(ByteString.copyFrom(String.valueOf(key % 1000), "UTF-8"))
                        .build())
                .build()
        def request = NotRemovedCachedItem.newBuilder()
                .setIsInput(IsInput.newBuilder()
                        .setIsId(IsId.newBuilder()
                                .setIsOutput(IsOutput.newBuilder()
                                        .setIsStringValue(String.valueOf(key))
                                        .build())
                                .build())
                        .build())
                .build()

        when:
        store.produce(storeRequest)

        and:
        remove.produce(request)

        then:
        notThrown StatusRuntimeException

    }
}
