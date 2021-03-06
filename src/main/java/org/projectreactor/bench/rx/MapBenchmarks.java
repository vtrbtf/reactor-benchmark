/*
 * Copyright (c) 2011-2015 Pivotal Software Inc., Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.projectreactor.bench.rx;

import org.openjdk.jmh.annotations.*;
import org.projectreactor.bench.rx.support.InputWithIncrementingLong;
import reactor.fn.Function;
import reactor.fn.Supplier;
import reactor.rx.Streams;
import reactor.rx.action.Action;
import reactor.rx.action.transformation.MapAction;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class MapBenchmarks {

	@State(Scope.Thread)
	public static class Input extends InputWithIncrementingLong {

		@Param({"1", "1000", "1000000"})
		public int size;

		@Override
		public int getSize() {
			return size;
		}

		public Supplier<Action<Long, Long>> map;

		@Override
		protected void postSetup() {
			map = () -> new MapAction<Long, Long>(IDENTITY_FUNCTION
			);
		}
	}

	@Benchmark
	public void mapPassThruViaConnect(Input input) throws InterruptedException {
		input.observable.lift(input.map).subscribe(input.observer);
	}

	@Benchmark
	public void mapInstance(Input input) {
		Streams.just(1l).map(IDENTITY_FUNCTION);
	}

	@Benchmark
	public void mapPassThru(Input input) throws InterruptedException {
		input.observable.map(IDENTITY_FUNCTION).subscribe(input.observer);
	}

	private static final Function<Long, Long> IDENTITY_FUNCTION = new Function<Long, Long>() {
		@Override
		public Long apply(Long value) {
			return value;
		}
	};
}
